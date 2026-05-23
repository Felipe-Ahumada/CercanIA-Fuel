package cl.fuelonline.user.api;

import cl.fuelonline.catalog.domain.model.FuelType;
import cl.fuelonline.user.application.dto.VehicleBrandResponse;
import cl.fuelonline.user.application.dto.VehicleModelResponse;
import cl.fuelonline.user.application.service.VehicleCatalogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehiculos")
@RequiredArgsConstructor
@Tag(name = "Vehicle Catalog", description = "Public catalog of vehicle brands, models and fuel types")
public class VehicleCatalogController {

    private final VehicleCatalogService service;

    @GetMapping("/marcas")
    public List<VehicleBrandResponse> brands() {
        return service.getBrands();
    }

    @GetMapping("/marcas/{brandId}/modelos")
    public List<VehicleModelResponse> models(@PathVariable Integer brandId) {
        return service.getModelsByBrand(brandId);
    }

    @GetMapping("/combustibles")
    public List<FuelType> fuelTypes() {
        return service.getFuelTypes();
    }
}
