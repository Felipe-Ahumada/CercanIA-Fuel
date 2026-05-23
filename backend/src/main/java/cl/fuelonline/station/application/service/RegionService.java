package cl.fuelonline.station.application.service;

import cl.fuelonline.station.domain.model.Commune;
import cl.fuelonline.station.domain.model.Region;
import cl.fuelonline.station.domain.repository.CommuneRepository;
import cl.fuelonline.station.domain.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionService {

    private final RegionRepository regionRepository;
    private final CommuneRepository communeRepository;

    public List<Region> listRegions() {
        return regionRepository.findAll()
                .stream()
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .toList();
    }

    public List<Commune> listCommunes(Integer regionId) {
        return communeRepository.findAllByRegion_IdOrderByNameAsc(regionId);
    }
}
