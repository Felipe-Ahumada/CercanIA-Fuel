package cl.fuelonline.station.api;

import cl.fuelonline.station.application.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Catalog", description = "Catálogos geográficos")
public class RegionController {

    private final RegionService regionService;

    @GetMapping("/api/v1/regiones")
    @Operation(summary = "Lista todas las regiones")
    public List<RegionDto> listRegions() {
        return regionService.listRegions().stream()
                .map(r -> new RegionDto(r.getId(), r.getName()))
                .toList();
    }

    @GetMapping("/api/v1/comunas")
    @Operation(summary = "Lista comunas por región")
    public List<CommuneDto> listCommunes(@RequestParam Integer regionId) {
        return regionService.listCommunes(regionId).stream()
                .map(c -> new CommuneDto(c.getId(), c.getName()))
                .toList();
    }

    public record RegionDto(Integer id, String name) {}
    public record CommuneDto(Integer id, String name) {}
}
