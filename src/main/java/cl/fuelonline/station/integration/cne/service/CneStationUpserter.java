package cl.fuelonline.station.integration.cne.service;

import cl.fuelonline.station.domain.model.*;
import cl.fuelonline.station.domain.repository.StationRepository;
import cl.fuelonline.station.domain.repository.PriceHistoryRepository;
import cl.fuelonline.station.integration.cne.dto.CneStationDto;
import cl.fuelonline.station.integration.cne.dto.CnePriceDto;
import cl.fuelonline.station.integration.cne.dto.CneLocationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

/**
 * Persiste UNA station CNE en una transaction propia.
 * Si una station falla, no contamina el resto.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CneStationUpserter {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter HORA  = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final StationRepository stationRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final CneCatalogResolver catalogs;

    /** Result of processing one station. */
    public record StationResult(boolean created, int pricesInserted, int pricesSkipped) {}

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public StationResult upsert(CneStationDto dto) {
        if (dto.code() == null || dto.code().isBlank()) {
            throw new IllegalArgumentException("Station without code");
        }
        if (dto.location() == null) {
            throw new IllegalArgumentException("Station " + dto.code() + " without location");
        }

        Brand brand = catalogs.resolveBrand(dto.distributor());
        Region region = catalogs.resolveRegion(dto.location());
        Commune commune = catalogs.resolveCommune(dto.location(), region);

        Optional<Station> existing = stationRepository.findByApiCode(dto.code());
        Station b;
        boolean created;

        if (existing.isPresent()) {
            b = existing.get();
            created = false;
            updateFields(b, dto, brand, commune);
        } else {
            b = newStation(dto, brand, commune);
            b = stationRepository.save(b);
            created = true;
        }

        b.setSyncAt(LocalDateTime.now());

        // Prices
        int insertados = 0, omitidos = 0;
        if (dto.prices() != null) {
            for (Map.Entry<String, CnePriceDto> entry : dto.prices().entrySet()) {
                if (processPrice(b, entry.getKey(), entry.getValue())) {
                    insertados++;
                } else {
                    omitidos++;
                }
            }
        }

        return new StationResult(created, insertados, omitidos);
    }

    private Station newStation(CneStationDto dto, Brand brand, Commune commune) {
        CneLocationDto u = dto.location();
        return Station.builder()
                .apiCode(dto.code())
                .name(stationName(dto))
                .brand(brand)
                .commune(commune)
                .address(u.address() != null ? u.address().trim() : "")
                .latitude(parseDecimal(u.latitude()))
                .longitude(parseDecimal(u.longitude()))
                .inMaintenance(dto.inMaintenanceBool())
                .active(Boolean.TRUE)
                .build();
    }

    private void updateFields(Station b, CneStationDto dto, Brand brand, Commune commune) {
        CneLocationDto u = dto.location();
        b.setName(stationName(dto));
        b.setBrand(brand);
        b.setCommune(commune);
        if (u.address() != null && !u.address().isBlank())
            b.setAddress(u.address().trim());
        BigDecimal lat = parseDecimal(u.latitude());
        BigDecimal lon = parseDecimal(u.longitude());
        if (lat != null) b.setLatitude(lat);
        if (lon != null) b.setLongitude(lon);
        b.setInMaintenance(dto.inMaintenanceBool());
    }

    private String stationName(CneStationDto dto) {
        if (dto.legalName() != null && !dto.legalName().isBlank())
            return dto.legalName().trim();
        if (dto.distributor() != null && dto.distributor().brand() != null)
            return dto.distributor().brand() + " " + dto.code();
        return dto.code();
    }

    /**
     * Inserta el price en historial solo si su apiTimestamp es estrictamente
     * more recent than the last one recorded for that (station, fuel) pair.
     * Returns true if inserted, false if skipped.
     */
    private boolean processPrice(Station b, String cneKey, CnePriceDto price) {
        if (price == null || price.price() == null) return false;

        BigDecimal value;
        try {
            value = new BigDecimal(price.price().trim());
        } catch (NumberFormatException ex) {
            log.warn("CNE: invalid price for {} ({}): {}", b.getApiCode(), cneKey, price.price());
            return false;
        }

        ChargeUnit unit = catalogs.parseChargeUnit(price.chargeUnit());
        FuelType type = catalogs.resolveFuel(cneKey, unit);

        LocalDateTime apiTs = parseDateTime(price.updateDate(), price.updateTime());
        if (apiTs == null) {
            log.warn("CNE: date/time invalida en {} ({})", b.getApiCode(), cneKey);
            return false;
        }

        Optional<PriceHistory> lastEntry = priceHistoryRepository
                .findFirstByStation_IdAndFuelType_IdOrderByApiTimestampDesc(
                        b.getId(), type.getId());

        if (lastEntry.isPresent() && !apiTs.isAfter(lastEntry.get().getApiTimestamp())) {
            return false; // we already have a newer or equal one
        }

        PriceHistory.AttentionType attention = "Asistido".equalsIgnoreCase(price.attentionType())
                ? PriceHistory.AttentionType.FULL
                : PriceHistory.AttentionType.SELF;

        priceHistoryRepository.save(PriceHistory.builder()
                .station(b)
                .fuelType(type)
                .price(value)
                .chargeUnit(unit)
                .attentionType(attention)
                .apiTimestamp(apiTs)
                .build());
        return true;
    }

    private static BigDecimal parseDecimal(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return new BigDecimal(s.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static LocalDateTime parseDateTime(String date, String time) {
        if (date == null || date.isBlank()) return null;
        try {
            LocalDate f = LocalDate.parse(date.trim(), DATE_FORMAT);
            LocalTime h = (time != null && !time.isBlank())
                    ? LocalTime.parse(time.trim(), HORA)
                    : LocalTime.MIDNIGHT;
            return LocalDateTime.of(f, h);
        } catch (Exception ex) {
            return null;
        }
    }
}
