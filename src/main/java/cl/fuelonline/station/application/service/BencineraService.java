package cl.fuelonline.station.application.service;

import cl.fuelonline.station.application.dto.*;
import cl.fuelonline.station.application.exception.BencineraYaExisteException;
import cl.fuelonline.station.application.mapper.BencineraMapper;
import cl.fuelonline.station.domain.model.Bencinera;
import cl.fuelonline.station.domain.model.Comuna;
import cl.fuelonline.station.domain.model.Marca;
import cl.fuelonline.station.domain.repository.BencineraRepository;
import cl.fuelonline.station.domain.repository.ComunaRepository;
import cl.fuelonline.station.domain.repository.MarcaRepository;
import cl.fuelonline.shared.exception.ResourceNotFoundException;
import cl.fuelonline.shared.util.GeoUtils;
import cl.fuelonline.shared.util.GeoUtils.BoundingBox;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BencineraService {

    private final BencineraRepository bencineraRepository;
    private final MarcaRepository marcaRepository;
    private final ComunaRepository comunaRepository;
    private final PrecioService precioService;
    private final BencineraMapper mapper;

    public Page<BencineraSummaryResponse> listar(Pageable pageable) {
        return bencineraRepository.findAll(pageable)
                .map(b -> mapper.toSummary(b, null));
    }

    public BencineraResponse buscarPorId(UUID id) {
        Bencinera b = obtener(id);
        return mapper.toResponse(b, precioService.preciosActualesDe(id));
    }

    public List<BencineraSummaryResponse> listarPorComuna(Integer comunaId) {
        return bencineraRepository.findAllByComuna_Id(comunaId).stream()
                .map(b -> mapper.toSummary(b, null))
                .toList();
    }

    /**
     * Bencineras dentro de un radio. Filtra primero con bounding-box (indice de
     * lat/lon) y luego refina con Haversine para distancia exacta. Devuelve
     * resultados ordenados por distancia ascendente.
     */
    public List<BencineraSummaryResponse> buscarCercanas(double lat, double lon, double radioKm) {
        BoundingBox box = GeoUtils.boundingBox(lat, lon, radioKm);
        return bencineraRepository.findEnBoundingBox(
                        box.latMin(), box.latMax(), box.lonMin(), box.lonMax()).stream()
                .map(b -> {
                    double d = GeoUtils.distanciaKm(lat, lon,
                            b.getLatitud().doubleValue(), b.getLongitud().doubleValue());
                    return mapper.toSummary(b, d);
                })
                .filter(s -> s.distanciaKm() <= radioKm)
                .sorted(Comparator.comparingDouble(BencineraSummaryResponse::distanciaKm))
                .toList();
    }

    @Transactional
    public BencineraResponse crear(BencineraCreateRequest req) {
        if (bencineraRepository.findByCodigoApi(req.codigoApi()).isPresent()) {
            throw new BencineraYaExisteException("codigoApi ya existe: " + req.codigoApi());
        }

        Marca marca = marcaRepository.findById(req.marcaId())
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada: " + req.marcaId()));
        Comuna comuna = comunaRepository.findById(req.comunaId())
                .orElseThrow(() -> new ResourceNotFoundException("Comuna no encontrada: " + req.comunaId()));

        Bencinera nueva = mapper.toEntity(req);
        nueva.setMarca(marca);
        nueva.setComuna(comuna);

        Bencinera persistida = bencineraRepository.save(nueva);
        return mapper.toResponse(persistida, List.of());
    }

    @Transactional
    public BencineraResponse actualizar(UUID id, BencineraUpdateRequest req) {
        Bencinera b = obtener(id);

        mapper.updateEntity(req, b);

        if (req.marcaId() != null) {
            Marca marca = marcaRepository.findById(req.marcaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada: " + req.marcaId()));
            b.setMarca(marca);
        }
        if (req.comunaId() != null) {
            Comuna comuna = comunaRepository.findById(req.comunaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comuna no encontrada: " + req.comunaId()));
            b.setComuna(comuna);
        }

        return mapper.toResponse(b, precioService.preciosActualesDe(id));
    }

    @Transactional
    public void eliminar(UUID id) {
        Bencinera b = obtener(id);
        b.setActivo(Boolean.FALSE);
    }

    Bencinera obtener(UUID id) {
        return bencineraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bencinera no encontrada: " + id));
    }
}
