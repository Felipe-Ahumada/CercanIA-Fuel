package cl.fuelonline.station.application.service;

import cl.fuelonline.station.application.dto.CurrentPriceResponse;
import cl.fuelonline.station.application.dto.PriceHistoryResponse;
import cl.fuelonline.station.application.dto.PriceRegisterRequest;
import cl.fuelonline.station.application.mapper.PriceMapper;
import cl.fuelonline.station.domain.model.Station;
import cl.fuelonline.station.domain.model.PriceHistory;
import cl.fuelonline.station.domain.model.FuelType;
import cl.fuelonline.station.domain.repository.StationRepository;
import cl.fuelonline.station.domain.repository.PriceHistoryRepository;
import cl.fuelonline.station.domain.repository.FuelTypeRepository;
import cl.fuelonline.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PriceService {

    private final PriceHistoryRepository priceRepository;
    private final StationRepository stationRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final PriceMapper mapper;

    public List<CurrentPriceResponse> currentPricesOf(UUID stationId) {
        return priceRepository.findCurrentPricesByFuel(stationId).stream()
                .map(mapper::toCurrent)
                .toList();
    }

    public CurrentPriceResponse currentPrice(UUID stationId, Integer fuelTypeId) {
        return priceRepository
                .findFirstByStation_IdAndFuelType_IdOrderByApiTimestampDesc(
                        stationId, fuelTypeId)
                .map(mapper::toCurrent)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No prices for station %s and fuel type %d"
                                .formatted(stationId, fuelTypeId)));
    }

    public Page<PriceHistoryResponse> history(UUID stationId, Integer fuelTypeId,
                                              Pageable pageable) {
        return priceRepository
                .findAllByStation_IdAndFuelType_IdOrderByApiTimestampDesc(
                        stationId, fuelTypeId, pageable)
                .map(mapper::toHistory);
    }

    @Transactional
    public PriceHistoryResponse register(UUID stationId, PriceRegisterRequest req) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Station not found: " + stationId));
        FuelType fuelType = fuelTypeRepository.findById(req.fuelTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Fuel type not found: " + req.fuelTypeId()));

        PriceHistory entity = PriceHistory.builder()
                .station(station)
                .fuelType(fuelType)
                .price(req.price())
                .chargeUnit(req.chargeUnit() != null ? req.chargeUnit() : fuelType.getChargeUnit())
                .attentionType(req.attentionType() != null
                        ? req.attentionType()
                        : PriceHistory.AttentionType.FULL)
                .apiTimestamp(req.apiTimestamp())
                .build();

        return mapper.toHistory(priceRepository.save(entity));
    }
}
