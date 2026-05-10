package cl.fuelonline.transaction.api;

import cl.fuelonline.transaction.application.dto.CalificacionCreateRequest;
import cl.fuelonline.transaction.application.dto.CalificacionResponse;
import cl.fuelonline.transaction.application.dto.CalificacionResumenResponse;
import cl.fuelonline.transaction.application.dto.CalificacionUpdateRequest;
import cl.fuelonline.transaction.application.service.CalificacionService;
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
public class CalificacionController {

    private final CalificacionService calificacionService;

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una calificacion por ID")
    public CalificacionResponse buscar(@PathVariable Long id) {
        return calificacionService.buscarPorId(id);
    }

    @GetMapping
    @Operation(summary = "Listar calificaciones por bencinera o usuario (uno de los dos es obligatorio)")
    public Page<CalificacionResponse> listar(
            @RequestParam(required = false) UUID bencineraId,
            @RequestParam(required = false) UUID usuarioId,
            @ParameterObject Pageable pageable) {
        if (bencineraId != null) {
            return calificacionService.listarPorBencinera(bencineraId, pageable);
        }
        if (usuarioId != null) {
            return calificacionService.listarPorUsuario(usuarioId, pageable);
        }
        throw new IllegalArgumentException("Debe enviar bencineraId o usuarioId");
    }

    @GetMapping("/bencinera/{bencineraId}/resumen")
    @Operation(summary = "Promedio y total de calificaciones de una bencinera")
    public CalificacionResumenResponse resumen(@PathVariable UUID bencineraId) {
        return calificacionService.resumen(bencineraId);
    }

    @PostMapping
    @Operation(summary = "Crear una calificacion (1 sola por usuario y bencinera)")
    public ResponseEntity<CalificacionResponse> crear(
            @Valid @RequestBody CalificacionCreateRequest req,
            UriComponentsBuilder uriBuilder) {
        CalificacionResponse creada = calificacionService.crear(req);
        URI location = uriBuilder.path("/api/v1/calificaciones/{id}")
                .buildAndExpand(creada.id())
                .toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una calificacion")
    public CalificacionResponse actualizar(@PathVariable Long id,
                                           @Valid @RequestBody CalificacionUpdateRequest req) {
        return calificacionService.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar una calificacion")
    public void eliminar(@PathVariable Long id) {
        calificacionService.eliminar(id);
    }
}
