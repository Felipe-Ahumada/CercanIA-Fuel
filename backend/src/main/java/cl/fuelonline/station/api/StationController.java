package cl.fuelonline.station.api;

import cl.fuelonline.station.application.dto.*;
import cl.fuelonline.station.application.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bencineras")
@RequiredArgsConstructor
@Validated
@Tag(name = "Stations", description = "Station catalog and geographic search")
public class StationController {

    private final StationService stationService;

    @GetMapping
    @Operation(summary = "List stations (paged)")
    public Page<StationSummaryResponse> list(@ParameterObject Pageable pageable) {
        return stationService.list(pageable);
    }

    @GetMapping("/cercanas")
    @Operation(summary = "Find stations within a radius (km), ordered by distance")
    public List<StationSummaryResponse> cercanas(
            @Parameter(description = "Latitude of the search point", example = "-33.4569")
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") double lat,

            @Parameter(description = "Longitude of the search point", example = "-70.6483")
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") double lon,

            @Parameter(description = "Radio en kilometros", example = "5.0")
            @RequestParam(defaultValue = "5.0") @DecimalMin("0.1") @DecimalMax("100.0") double radioKm) {
        return stationService.findNearby(lat, lon, radioKm);
    }

    @GetMapping("/commune/{communeId}")
    @Operation(summary = "List stations in a commune")
    public List<StationSummaryResponse> porComuna(@PathVariable @Positive Integer communeId) {
        return stationService.listarPorComuna(communeId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de una station, incluyendo prices actuales")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Station encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe", content = @Content)
    })
    public StationResponse find(@PathVariable UUID id) {
        return stationService.findById(id);
    }

    @PostMapping
    @Operation(summary = "Register a new station (admin or sync use)")
    public ResponseEntity<StationResponse> create(@Valid @RequestBody StationCreateRequest req,
                                                   UriComponentsBuilder uriBuilder) {
        StationResponse created = stationService.create(req);
        URI location = uriBuilder.path("/api/v1/bencineras/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de una station")
    public StationResponse update(@PathVariable UUID id,
                                        @Valid @RequestBody StationUpdateRequest req) {
        return stationService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Soft delete: marks the station as inactive")
    public void delete(@PathVariable UUID id) {
        stationService.delete(id);
    }
}
