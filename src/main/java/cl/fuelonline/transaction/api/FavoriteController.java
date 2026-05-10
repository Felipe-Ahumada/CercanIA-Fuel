package cl.fuelonline.transaction.api;

import cl.fuelonline.transaction.application.dto.FavoriteCreateRequest;
import cl.fuelonline.transaction.application.dto.FavoriteResponse;
import cl.fuelonline.transaction.application.service.FavoriteService;
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

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/favoritos")
@RequiredArgsConstructor
@Tag(name = "Favoritos", description = "Bencineras favoritas del user")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    @Operation(summary = "Listar bencineras favoritas de un user")
    public Page<FavoriteResponse> list(@RequestParam UUID userId,
                                         @ParameterObject Pageable pageable) {
        return favoriteService.listByUser(userId, pageable);
    }

    @GetMapping("/check")
    @Operation(summary = "Verificar si una station esta en favorites")
    public Map<String, Boolean> isFavorite(@RequestParam UUID userId,
                                           @RequestParam UUID stationId) {
        return Map.of("favorite", favoriteService.isFavorite(userId, stationId));
    }

    @PostMapping
    @Operation(summary = "Agregar una station a favorites")
    public ResponseEntity<FavoriteResponse> add(@Valid @RequestBody FavoriteCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(favoriteService.add(req));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Quitar una station de favorites")
    public void remove(@RequestParam UUID userId,
                       @RequestParam UUID stationId) {
        favoriteService.remove(userId, stationId);
    }
}
