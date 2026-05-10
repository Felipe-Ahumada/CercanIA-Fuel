package cl.fuelonline.transaction.api;

import cl.fuelonline.transaction.application.dto.AlertaCreateRequest;
import cl.fuelonline.transaction.application.dto.AlertaResponse;
import cl.fuelonline.transaction.application.service.AlertaService;
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
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/alertas")
@RequiredArgsConstructor
@Tag(name = "Alertas", description = "Notificaciones del usuario (precios, descuentos, sistema, etc.)")
public class AlertaController {

    private final AlertaService alertaService;

    @GetMapping
    @Operation(summary = "Listar alertas de un usuario, opcionalmente filtradas por estado leido")
    public Page<AlertaResponse> listar(
            @RequestParam UUID usuarioId,
            @RequestParam(required = false) Boolean leida,
            @ParameterObject Pageable pageable) {
        return alertaService.listarPorUsuario(usuarioId, leida, pageable);
    }

    @GetMapping("/no-leidas/count")
    @Operation(summary = "Cantidad de alertas no leidas para badges")
    public Map<String, Long> contarNoLeidas(@RequestParam UUID usuarioId) {
        return Map.of("noLeidas", alertaService.contarNoLeidas(usuarioId));
    }

    @PostMapping
    @Operation(summary = "Crear una alerta (uso interno o admin)")
    public ResponseEntity<AlertaResponse> crear(
            @Valid @RequestBody AlertaCreateRequest req,
            UriComponentsBuilder uriBuilder) {
        AlertaResponse creada = alertaService.crear(req);
        URI location = uriBuilder.path("/api/v1/alertas/{id}")
                .buildAndExpand(creada.id())
                .toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @PatchMapping("/{id}/leida")
    @Operation(summary = "Marcar una alerta como leida")
    public AlertaResponse marcarLeida(@PathVariable Long id) {
        return alertaService.marcarLeida(id);
    }

    @PatchMapping("/leidas")
    @Operation(summary = "Marcar todas las alertas del usuario como leidas")
    public Map<String, Integer> marcarTodasLeidas(@RequestParam UUID usuarioId) {
        return Map.of("actualizadas", alertaService.marcarTodasLeidas(usuarioId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar una alerta")
    public void eliminar(@PathVariable Long id) {
        alertaService.eliminar(id);
    }
}
