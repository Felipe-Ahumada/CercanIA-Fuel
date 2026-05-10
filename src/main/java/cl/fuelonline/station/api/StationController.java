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
@Tag(name = "Bencineras", description = "Catalogo de bencineras y busqueda geografica")
public class StationController {

    private final StationService bencineraService;

    @GetMapping
    @Operation(summary = "Listar bencineras paginado")
    public Page<StationSummaryResponse> listar(@ParameterObject Pageable pageable) {
        return bencineraService.listar(pageable);
    }

    @GetMapping("/cercanas")
    @Operation(summary = "Buscar bencineras dentro de un radio (km), ordenadas por distancia")
    public List<StationSummaryResponse> cercanas(
            @Parameter(description = "Latitud del punto de busqueda", example = "-33.4569")
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") double lat,

            @Parameter(description = "Longitud del punto de busqueda", example = "-70.6483")
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") double lon,

            @Parameter(description = "Radio en kilometros", example = "5.0")
            @RequestParam(defaultValue = "5.0") @DecimalMin("0.1") @DecimalMax("100.0") double radioKm) {
        return bencineraService.buscarCercanas(lat, lon, radioKm);
    }

    @GetMapping("/comuna/{comunaId}")
    @Operation(summary = "Listar bencineras de una comuna")
    public List<StationSummaryResponse> porComuna(@PathVariable @Positive Integer comunaId) {
        return bencineraService.listarPorComuna(comunaId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de una bencinera, incluyendo precios actuales")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Station encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe", content = @Content)
    })
    public StationResponse buscar(@PathVariable UUID id) {
        return bencineraService.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Registrar una nueva bencinera (uso administrativo o sync)")
    public ResponseEntity<StationResponse> crear(@Valid @RequestBody StationCreateRequest req,
                                                   UriComponentsBuilder uriBuilder) {
        StationResponse creada = bencineraService.crear(req);
        URI location = uriBuilder.path("/api/v1/bencineras/{id}").buildAndExpand(creada.id()).toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de una bencinera")
    public StationResponse actualizar(@PathVariable UUID id,
                                        @Valid @RequestBody StationUpdateRequest req) {
        return bencineraService.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Borrado logico: marca la bencinera como inactiva")
    public void eliminar(@PathVariable UUID id) {
        bencineraService.eliminar(id);
    }
}
