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
@Tag(name = "Transactions", description = "Fuel fill records and expense summary")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/{id}")
    @Operation(summary = "Get a transaction by ID")
    public TransactionResponse find(@PathVariable UUID id) {
        return transactionService.findById(id);
    }

    @GetMapping
    @Operation(summary = "List a user transactions, optionally between dates")
    public Page<TransactionResponse> list(
            @RequestParam UUID userId,
            @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @ParameterObject Pageable pageable) {
        if (desde != null && hasta != null) {
            return transactionService.listByUserBetween(userId, desde, hasta, pageable);
        }
        return transactionService.listByUser(userId, pageable);
    }

    @GetMapping("/summary")
    @Operation(summary = "Aggregated expense and savings summary by date range")
    public ExpenseSummaryResponse summary(
            @RequestParam UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return transactionService.expenseSummary(userId, desde, hasta);
    }

    @PostMapping
    @Operation(summary = "Register a new transaction (fuel fill)")
    public ResponseEntity<TransactionResponse> register(
            @Valid @RequestBody TransactionCreateRequest req,
            UriComponentsBuilder uriBuilder) {
        TransactionResponse created = transactionService.register(req);
        URI location = uriBuilder.path("/api/v1/transacciones/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar una transaction")
    public void delete(@PathVariable UUID id) {
        transactionService.delete(id);
    }
}
