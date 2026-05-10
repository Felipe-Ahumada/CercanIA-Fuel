package cl.fuelonline.station.application.service;

import cl.fuelonline.station.application.dto.*;
import cl.fuelonline.station.application.exception.StationAlreadyExistsException;
import cl.fuelonline.station.application.mapper.StationMapper;
import cl.fuelonline.station.domain.model.Station;
import cl.fuelonline.station.domain.model.Commune;
import cl.fuelonline.station.domain.model.Brand;
import cl.fuelonline.station.domain.repository.StationRepository;
import cl.fuelonline.station.domain.repository.CommuneRepository;
import cl.fuelonline.station.domain.repository.BrandRepository;
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
public class StationService {

    private final StationRepository bencineraRepository;
    private final BrandRepository marcaRepository;
    private final CommuneRepository comunaRepository;
    private final PriceService precioService;
    private final StationMapper mapper;

    public Page<StationSummaryResponse> listar(Pageable pageable) {
        return bencineraRepository.findAll(pageable)
                .map(b -> mapper.toSummary(b, null));
    }

    public StationResponse buscarPorId(UUID id) {
        Station b = obtener(id);
        return mapper.toResponse(b, precioService.preciosActualesDe(id));
    }

    public List<StationSummaryResponse> listarPorComuna(Integer comunaId) {
        return bencineraRepository.findAllByComuna_Id(comunaId).stream()
                .map(b -> mapper.toSummary(b, null))
                .toList();
    }

    /**
     * Bencineras dentro de un radio. Filtra primero con bounding-box (indice de
     * lat/lon) y luego refina con Haversine para distancia exacta. Devuelve
     * resultados ordenados por distancia ascendente.
     */
    public List<StationSummaryResponse> buscarCercanas(double lat, double lon, double radioKm) {
        BoundingBox box = GeoUtils.boundingBox(lat, lon, radioKm);
        return bencineraRepository.findEnBoundingBox(
                        box.latMin(), box.latMax(), box.lonMin(), box.lonMax()).stream()
                .map(b -> {
                    double d = GeoUtils.distanciaKm(lat, lon,
                            b.getLatitud().doubleValue(), b.getLongitud().doubleValue());
                    return mapper.toSummary(b, d);
                })
                .filter(s -> s.distanciaKm() <= radioKm)
                .sorted(Comparator.comparingDouble(StationSummaryResponse::distanciaKm))
                .toList();
    }

    @Transactional
    public StationResponse crear(StationCreateRequest req) {
        if (bencineraRepository.findByCodigoApi(req.codigoApi()).isPresent()) {
            throw new StationAlreadyExistsException("codigoApi ya existe: " + req.codigoApi());
        }

        Brand marca = marcaRepository.findById(req.marcaId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand no encontrada: " + req.marcaId()));
        Commune comuna = comunaRepository.findById(req.comunaId())
                .orElseThrow(() -> new ResourceNotFoundException("Commune no encontrada: " + req.comunaId()));

        Station nueva = mapper.toEntity(req);
        nueva.setMarca(marca);
        nueva.setComuna(comuna);

        Station persistida = bencineraRepository.save(nueva);
        return mapper.toResponse(persistida, List.of());
    }

    @Transactional
    public StationResponse actualizar(UUID id, StationUpdateRequest req) {
        Station b = obtener(id);

        mapper.updateEntity(req, b);

        if (req.marcaId() != null) {
            Brand marca = marcaRepository.findById(req.marcaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand no encontrada: " + req.marcaId()));
            b.setMarca(marca);
        }
        if (req.comunaId() != null) {
            Commune comuna = comunaRepository.findById(req.comunaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Commune no encontrada: " + req.comunaId()));
            b.setComuna(comuna);
        }

        return mapper.toResponse(b, precioService.preciosActualesDe(id));
    }

    @Transactional
    public void eliminar(UUID id) {
        Station b = obtener(id);
        b.setActivo(Boolean.FALSE);
    }

    Station obtener(UUID id) {
        return bencineraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station no encontrada: " + id));
    }
}
