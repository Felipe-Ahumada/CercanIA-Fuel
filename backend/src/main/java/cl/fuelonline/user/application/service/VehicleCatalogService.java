package cl.fuelonline.user.application.service;

import cl.fuelonline.catalog.domain.model.FuelType;
import cl.fuelonline.catalog.domain.repository.FuelTypeRepository;
import cl.fuelonline.user.application.dto.VehicleBrandResponse;
import cl.fuelonline.user.application.dto.VehicleModelResponse;
import cl.fuelonline.user.application.mapper.VehicleCatalogMapper;
import cl.fuelonline.user.domain.repository.VehicleBrandRepository;
import cl.fuelonline.user.domain.repository.VehicleModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleCatalogService {

    private final VehicleBrandRepository brandRepository;
    private final VehicleModelRepository modelRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final VehicleCatalogMapper catalogMapper;

    public List<VehicleBrandResponse> getBrands() {
        return brandRepository.findAllByOrderByNameAsc().stream()
                .map(catalogMapper::toBrandResponse)
                .toList();
    }

    public List<VehicleModelResponse> getModelsByBrand(Integer brandId) {
        return modelRepository.findByBrand_IdOrderByNameAsc(brandId).stream()
                .map(catalogMapper::toModelResponse)
                .toList();
    }

    public List<FuelType> getFuelTypes() {
        List<String> canonical = List.of("93", "95", "97", "DI", "GNV");
        Map<String, FuelType> byShortName = new LinkedHashMap<>();
        for (FuelType ft : fuelTypeRepository.findAll()) {
            byShortName.merge(ft.getShortName().toUpperCase(), ft,
                (a, b) -> a.getId() < b.getId() ? a : b);
        }
        return canonical.stream()
                .map(sn -> byShortName.get(sn.toUpperCase()))
                .filter(java.util.Objects::nonNull)
                .toList();
    }
}