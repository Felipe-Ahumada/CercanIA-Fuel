package cl.fuelonline.station.application.service;

import cl.fuelonline.station.application.dto.CurrentPriceResponse;
import cl.fuelonline.station.application.dto.PriceHistoryResponse;
import cl.fuelonline.station.application.dto.RegistrarPrecioRequest;
import cl.fuelonline.station.application.mapper.PriceMapper;
import cl.fuelonline.station.domain.model.Station;
import cl.fuelonline.station.domain.model.PriceHistory;
import cl.fuelonline.station.domain.model.FuelType;
import cl.fuelonline.station.domain.repository.StationRepository;
import cl.fuelonline.station.domain.repository.PrecioHistorialRepository;
import cl.fuelonline.station.domain.repository.TipoCombustibleRepository;
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

    private final PrecioHistorialRepository precioRepository;
    private final StationRepository bencineraRepository;
    private final TipoCombustibleRepository tipoCombustibleRepository;
    private final PriceMapper mapper;

    public List<CurrentPriceResponse> preciosActualesDe(UUID bencineraId) {
        return precioRepository.findUltimosPreciosPorCombustible(bencineraId).stream()
                .map(mapper::toActual)
                .toList();
    }

    public CurrentPriceResponse precioActual(UUID bencineraId, Integer tipoCombustibleId) {
        return precioRepository
                .findFirstByBencinera_IdAndTipoCombustible_IdOrderByApiTimestampDesc(
                        bencineraId, tipoCombustibleId)
                .map(mapper::toActual)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sin precios para bencinera %s y combustible %d"
                                .formatted(bencineraId, tipoCombustibleId)));
    }

    public Page<PriceHistoryResponse> historial(UUID bencineraId, Integer tipoCombustibleId,
                                                   Pageable pageable) {
        return precioRepository
                .findAllByBencinera_IdAndTipoCombustible_IdOrderByApiTimestampDesc(
                        bencineraId, tipoCombustibleId, pageable)
                .map(mapper::toHistorial);
    }

    @Transactional
    public PriceHistoryResponse registrar(UUID bencineraId, RegistrarPrecioRequest req) {
        Station bencinera = bencineraRepository.findById(bencineraId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Station no encontrada: " + bencineraId));
        FuelType combustible = tipoCombustibleRepository.findById(req.tipoCombustibleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de combustible no encontrado: " + req.tipoCombustibleId()));

        PriceHistory nuevo = PriceHistory.builder()
                .bencinera(bencinera)
                .tipoCombustible(combustible)
                .precio(req.precio())
                .unidadCobro(req.unidadCobro() != null ? req.unidadCobro() : combustible.getUnidadCobro())
                .tipoAtencion(req.tipoAtencion() != null
                        ? req.tipoAtencion()
                        : PriceHistory.TipoAtencion.FULL)
                .apiTimestamp(req.apiTimestamp())
                .build();

        return mapper.toHistorial(precioRepository.save(nuevo));
    }
}
