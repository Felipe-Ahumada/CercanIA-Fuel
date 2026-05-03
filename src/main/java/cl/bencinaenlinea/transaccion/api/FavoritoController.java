package cl.bencinaenlinea.transaccion.api;

import cl.bencinaenlinea.transaccion.application.dto.FavoritoCreateRequest;
import cl.bencinaenlinea.transaccion.application.dto.FavoritoResponse;
import cl.bencinaenlinea.transaccion.application.service.FavoritoService;
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
@Tag(name = "Favoritos", description = "Bencineras favoritas del usuario")
public class FavoritoController {

    private final FavoritoService favoritoService;

    @GetMapping
    @Operation(summary = "Listar bencineras favoritas de un usuario")
    public Page<FavoritoResponse> listar(@RequestParam UUID usuarioId,
                                         @ParameterObject Pageable pageable) {
        return favoritoService.listarPorUsuario(usuarioId, pageable);
    }

    @GetMapping("/check")
    @Operation(summary = "Verificar si una bencinera esta en favoritos")
    public Map<String, Boolean> esFavorito(@RequestParam UUID usuarioId,
                                           @RequestParam UUID bencineraId) {
        return Map.of("favorito", favoritoService.esFavorito(usuarioId, bencineraId));
    }

    @PostMapping
    @Operation(summary = "Agregar una bencinera a favoritos")
    public ResponseEntity<FavoritoResponse> agregar(@Valid @RequestBody FavoritoCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(favoritoService.agregar(req));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Quitar una bencinera de favoritos")
    public void quitar(@RequestParam UUID usuarioId,
                       @RequestParam UUID bencineraId) {
        favoritoService.quitar(usuarioId, bencineraId);
    }
}
