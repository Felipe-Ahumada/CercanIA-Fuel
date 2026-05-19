package cl.fuelonline.station.application.service;

import cl.fuelonline.station.application.dto.*;
import cl.fuelonline.station.application.exception.StationAlreadyExistsException;
import cl.fuelonline.station.application.mapper.StationMapper;
import cl.fuelonline.station.domain.model.Station;
import cl.fuelonline.station.domain.model.Commune;
import cl.fuelonline.catalog.domain.model.Brand;
import cl.fuelonline.station.domain.repository.StationRepository;
import cl.fuelonline.station.domain.repository.CommuneRepository;
import cl.fuelonline.catalog.domain.repository.BrandRepository;
import cl.fuelonline.shared.exception.ResourceNotFoundException;
import cl.fuelonline.shared.util.GeoUtils;
import cl.fuelonline.shared.util.GeoUtils.BoundingBox;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StationService {

    private final StationRepository stationRepository;
    private final BrandRepository brandRepository;
    private final CommuneRepository communeRepository;
    private final PriceService priceService;
    private final StationMapper mapper;

    public Page<StationSummaryResponse> list(Pageable pageable) {
        Page<Station> page = stationRepository.findAllWithRelations(pageable);
        Set<UUID> ids = page.stream().map(Station::getId).collect(Collectors.toSet());
        Map<UUID, List<CurrentPriceResponse>> pricesMap = priceService.currentPricesOfBatch(ids);
        return page.map(b -> mapper.toSummary(b, null,
                pricesMap.getOrDefault(b.getId(), List.of())));
    }

    public StationResponse findById(UUID id) {
        Station b = get(id);
        return mapper.toResponse(b, priceService.currentPricesOf(id));
    }

    public List<StationSummaryResponse> listarPorComuna(Integer communeId) {
        List<Station> stations = stationRepository.findAllByCommune_Id(communeId);
        Set<UUID> ids = stations.stream().map(Station::getId).collect(Collectors.toSet());
        Map<UUID, List<CurrentPriceResponse>> pricesMap = priceService.currentPricesOfBatch(ids);
        return stations.stream()
                .map(b -> mapper.toSummary(b, null,
                        pricesMap.getOrDefault(b.getId(), List.of())))
                .toList();
    }

    /**
     * Bencineras dentro de un radio. Filtra primero con bounding-box (indice de
     * lat/lon) y luego refina con Haversine para distancia exacta. Devuelve
     * resultados ordenados por distancia ascendente.
     */
    private static final int PRICE_STALE_DAYS = 30;

    public List<StationSummaryResponse> findNearby(double lat, double lon, double radioKm) {
        BoundingBox box = GeoUtils.boundingBox(lat, lon, radioKm);
        LocalDateTime threshold = LocalDateTime.now().minusDays(PRICE_STALE_DAYS);
        List<Station> candidates = stationRepository.findInBoundingBox(
                box.latMin(), box.latMax(), box.lonMin(), box.lonMax(), threshold);
        Set<UUID> ids = candidates.stream().map(Station::getId).collect(Collectors.toSet());
        Map<UUID, List<CurrentPriceResponse>> pricesMap = priceService.currentPricesOfBatch(ids);
        return candidates.stream()
                .map(b -> {
                    double d = GeoUtils.distanciaKm(lat, lon,
                            b.getLatitude().doubleValue(), b.getLongitude().doubleValue());
                    return mapper.toSummary(b, d,
                            pricesMap.getOrDefault(b.getId(), List.of()));
                })
                .filter(s -> s.distanciaKm() <= radioKm)
                .sorted(Comparator.comparingDouble(StationSummaryResponse::distanciaKm))
                .toList();
    }

    @Transactional
    public StationResponse create(StationCreateRequest req) {
        if (stationRepository.findByApiCode(req.apiCode()).isPresent()) {
            throw new StationAlreadyExistsException("apiCode ya existe: " + req.apiCode());
        }

        Brand brand = brandRepository.findById(req.brandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found: " + req.brandId()));
        Commune commune = communeRepository.findById(req.communeId())
                .orElseThrow(() -> new ResourceNotFoundException("Commune not found: " + req.communeId()));

        Station nueva = mapper.toEntity(req);
        nueva.setBrand(brand);
        nueva.setCommune(commune);

        Station persistida = stationRepository.save(nueva);
        return mapper.toResponse(persistida, List.of());
    }

    @Transactional
    public StationResponse update(UUID id, StationUpdateRequest req) {
        Station b = get(id);

        mapper.updateEntity(req, b);

        if (req.brandId() != null) {
            Brand brand = brandRepository.findById(req.brandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand not found: " + req.brandId()));
            b.setBrand(brand);
        }
        if (req.communeId() != null) {
            Commune commune = communeRepository.findById(req.communeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Commune not found: " + req.communeId()));
            b.setCommune(commune);
        }

        return mapper.toResponse(b, priceService.currentPricesOf(id));
    }

    @Transactional
    public void delete(UUID id) {
        Station b = get(id);
        b.setActive(Boolean.FALSE);
    }

    Station get(UUID id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station not found: " + id));
    }
}
