package cl.fuelonline.transaction.api;

import cl.fuelonline.transaction.application.dto.ExpenseSummaryResponse;
import cl.fuelonline.transaction.application.dto.TransactionCreateRequest;
import cl.fuelonline.transaction.application.dto.TransactionResponse;
import cl.fuelonline.transaction.application.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transacciones")
@RequiredArgsConstructor
@Tag(name = "Transacciones", description = "Registro de cargas de combustible y resumen de gasto")
public class TransactionController {

    private final TransactionService transaccionService;

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una transaccion por ID")
    public TransactionResponse buscar(@PathVariable UUID id) {
        return transaccionService.buscarPorId(id);
    }

    @GetMapping
    @Operation(summary = "Listar transacciones de un usuario, opcionalmente entre fechas")
    public Page<TransactionResponse> listar(
            @RequestParam UUID usuarioId,
            @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @ParameterObject Pageable pageable) {
        if (desde != null && hasta != null) {
            return transaccionService.listarPorUsuarioEntre(usuarioId, desde, hasta, pageable);
        }
        return transaccionService.listarPorUsuario(usuarioId, pageable);
    }

    @GetMapping("/resumen")
    @Operation(summary = "Resumen agregado de gasto y ahorro por rango de fechas")
    public ExpenseSummaryResponse resumen(
            @RequestParam UUID usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return transaccionService.resumenGasto(usuarioId, desde, hasta);
    }

    @PostMapping
    @Operation(summary = "Registrar una nueva transaccion (carga de combustible)")
    public ResponseEntity<TransactionResponse> registrar(
            @Valid @RequestBody TransactionCreateRequest req,
            UriComponentsBuilder uriBuilder) {
        TransactionResponse creada = transaccionService.registrar(req);
        URI location = uriBuilder.path("/api/v1/transacciones/{id}")
                .buildAndExpand(creada.id())
                .toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar una transaccion")
    public void eliminar(@PathVariable UUID id) {
        transaccionService.eliminar(id);
    }
}
