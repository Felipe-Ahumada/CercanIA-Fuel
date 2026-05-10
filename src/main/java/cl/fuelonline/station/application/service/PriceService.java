package cl.fuelonline.station.application.service;

import cl.fuelonline.station.application.dto.CurrentPriceResponse;
import cl.fuelonline.station.application.dto.PriceHistoryResponse;
import cl.fuelonline.station.application.dto.RegistrarPrecioRequest;
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

    private final PriceHistoryRepository precioRepository;
    private final StationRepository stationRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final PriceMapper mapper;

    public List<CurrentPriceResponse> preciosActualesDe(UUID stationId) {
        return precioRepository.findCurrentPricesByFuel(stationId).stream()
                .map(mapper::toActual)
                .toList();
    }

    public CurrentPriceResponse precioActual(UUID stationId, Integer fuelTypeId) {
        return precioRepository
                .findFirstByStation_IdAndFuelType_IdOrderByApiTimestampDesc(
                        stationId, fuelTypeId)
                .map(mapper::toActual)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No prices for station %s and fuel type %d"
                                .formatted(stationId, fuelTypeId)));
    }

    public Page<PriceHistoryResponse> historial(UUID stationId, Integer fuelTypeId,
                                                   Pageable pageable) {
        return precioRepository
                .findAllByStation_IdAndFuelType_IdOrderByApiTimestampDesc(
                        stationId, fuelTypeId, pageable)
                .map(mapper::toHistorial);
    }

    @Transactional
    public PriceHistoryResponse register(UUID stationId, RegistrarPrecioRequest req) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Station not found: " + stationId));
        FuelType combustible = fuelTypeRepository.findById(req.fuelTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Fuel type not found: " + req.fuelTypeId()));

        PriceHistory nuevo = PriceHistory.builder()
                .station(station)
                .fuelType(combustible)
                .price(req.price())
                .chargeUnit(req.chargeUnit() != null ? req.chargeUnit() : combustible.getChargeUnit())
                .attentionType(req.attentionType() != null
                        ? req.attentionType()
                        : PriceHistory.TipoAtencion.FULL)
                .apiTimestamp(req.apiTimestamp())
                .build();

        return mapper.toHistorial(precioRepository.save(nuevo));
    }
}
