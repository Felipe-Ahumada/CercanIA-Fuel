package cl.fuelonline.transaction.api;

import cl.fuelonline.transaction.application.dto.RatingCreateRequest;
import cl.fuelonline.transaction.application.dto.RatingResponse;
import cl.fuelonline.transaction.application.dto.RatingSummaryResponse;
import cl.fuelonline.transaction.application.dto.RatingUpdateRequest;
import cl.fuelonline.transaction.application.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/calificaciones")
@RequiredArgsConstructor
@Tag(name = "Calificaciones", description = "Resenas de bencineras por parte de los usuarios")
public class RatingController {

    private final RatingService ratingService;

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una rating por ID")
    public RatingResponse find(@PathVariable Long id) {
        return ratingService.findById(id);
    }

    @GetMapping
    @Operation(summary = "Listar ratings por station o user (uno de los dos es obligatorio)")
    public Page<RatingResponse> list(
            @RequestParam(required = false) UUID stationId,
            @RequestParam(required = false) UUID userId,
            @ParameterObject Pageable pageable) {
        if (stationId != null) {
            return ratingService.listByStation(stationId, pageable);
        }
        if (userId != null) {
            return ratingService.listByUser(userId, pageable);
        }
        throw new IllegalArgumentException("Debe enviar stationId o userId");
    }

    @GetMapping("/station/{stationId}/summary")
    @Operation(summary = "Promedio y total de ratings de una station")
    public RatingSummaryResponse summary(@PathVariable UUID stationId) {
        return ratingService.summary(stationId);
    }

    @PostMapping
    @Operation(summary = "Crear una rating (1 sola por user y station)")
    public ResponseEntity<RatingResponse> create(
            @Valid @RequestBody RatingCreateRequest req,
            UriComponentsBuilder uriBuilder) {
        RatingResponse created = ratingService.create(req);
        URI location = uriBuilder.path("/api/v1/ratings/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una rating")
    public RatingResponse update(@PathVariable Long id,
                                           @Valid @RequestBody RatingUpdateRequest req) {
        return ratingService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar una rating")
    public void delete(@PathVariable Long id) {
        ratingService.delete(id);
    }
}
