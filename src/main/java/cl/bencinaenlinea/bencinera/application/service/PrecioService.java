package cl.bencinaenlinea.bencinera.application.service;

import cl.bencinaenlinea.bencinera.application.dto.PrecioActualResponse;
import cl.bencinaenlinea.bencinera.application.dto.PrecioHistorialResponse;
import cl.bencinaenlinea.bencinera.application.dto.RegistrarPrecioRequest;
import cl.bencinaenlinea.bencinera.application.mapper.PrecioMapper;
import cl.bencinaenlinea.bencinera.domain.model.Bencinera;
import cl.bencinaenlinea.bencinera.domain.model.PrecioHistorial;
import cl.bencinaenlinea.bencinera.domain.model.TipoCombustible;
import cl.bencinaenlinea.bencinera.domain.repository.BencineraRepository;
import cl.bencinaenlinea.bencinera.domain.repository.PrecioHistorialRepository;
import cl.bencinaenlinea.bencinera.domain.repository.TipoCombustibleRepository;
import cl.bencinaenlinea.shared.exception.ResourceNotFoundException;
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
public class PrecioService {

    private final PrecioHistorialRepository precioRepository;
    private final BencineraRepository bencineraRepository;
    private final TipoCombustibleRepository tipoCombustibleRepository;
    private final PrecioMapper mapper;

    public List<PrecioActualResponse> preciosActualesDe(UUID bencineraId) {
        return precioRepository.findUltimosPreciosPorCombustible(bencineraId).stream()
                .map(mapper::toActual)
                .toList();
    }

    public PrecioActualResponse precioActual(UUID bencineraId, Integer tipoCombustibleId) {
        return precioRepository
                .findFirstByBencinera_IdAndTipoCombustible_IdOrderByApiTimestampDesc(
                        bencineraId, tipoCombustibleId)
                .map(mapper::toActual)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sin precios para bencinera %s y combustible %d"
                                .formatted(bencineraId, tipoCombustibleId)));
    }

    public Page<PrecioHistorialResponse> historial(UUID bencineraId, Integer tipoCombustibleId,
                                                   Pageable pageable) {
        return precioRepository
                .findAllByBencinera_IdAndTipoCombustible_IdOrderByApiTimestampDesc(
                        bencineraId, tipoCombustibleId, pageable)
                .map(mapper::toHistorial);
    }

    @Transactional
    public PrecioHistorialResponse registrar(UUID bencineraId, RegistrarPrecioRequest req) {
        Bencinera bencinera = bencineraRepository.findById(bencineraId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bencinera no encontrada: " + bencineraId));
        TipoCombustible combustible = tipoCombustibleRepository.findById(req.tipoCombustibleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de combustible no encontrado: " + req.tipoCombustibleId()));

        PrecioHistorial nuevo = PrecioHistorial.builder()
                .bencinera(bencinera)
                .tipoCombustible(combustible)
                .precio(req.precio())
                .unidadCobro(req.unidadCobro() != null ? req.unidadCobro() : combustible.getUnidadCobro())
                .tipoAtencion(req.tipoAtencion() != null
                        ? req.tipoAtencion()
                        : PrecioHistorial.TipoAtencion.FULL)
                .apiTimestamp(req.apiTimestamp())
                .build();

        return mapper.toHistorial(precioRepository.save(nuevo));
    }
}
