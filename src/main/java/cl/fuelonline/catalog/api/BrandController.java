package cl.fuelonline.catalog.api;

import cl.fuelonline.catalog.application.dto.BrandResponse;
import cl.fuelonline.catalog.application.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/marcas")
@RequiredArgsConstructor
@Tag(name = "Brands", description = "Catálogo de marcas de bencinera")
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    @Operation(summary = "Lista todas las marcas activas")
    public List<BrandResponse> listAll() {
        return brandService.listAll();
    }
}
