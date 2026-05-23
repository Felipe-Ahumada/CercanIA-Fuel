package cl.fuelonline.transaction.api;

import cl.fuelonline.transaction.application.dto.AlertCreateRequest;
import cl.fuelonline.transaction.application.dto.AlertResponse;
import cl.fuelonline.transaction.application.service.AlertService;
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
@Tag(name = "Alerts", description = "User notifications (prices, discounts, system, etc.)")
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    @Operation(summary = "List a user alerts, optionally filtered by read status")
    public Page<AlertResponse> list(
            @RequestParam UUID userId,
            @RequestParam(required = false) Boolean read,
            @ParameterObject Pageable pageable) {
        return alertService.listByUser(userId, read, pageable);
    }

    @GetMapping("/no-leidas/count")
    @Operation(summary = "Number of unread alerts (for badges)")
    public Map<String, Long> countUnread(@RequestParam UUID userId) {
        return Map.of("noLeidas", alertService.countUnread(userId));
    }

    @PostMapping
    @Operation(summary = "Create an alert (internal or admin use)")
    public ResponseEntity<AlertResponse> create(
            @Valid @RequestBody AlertCreateRequest req,
            UriComponentsBuilder uriBuilder) {
        AlertResponse created = alertService.create(req);
        URI location = uriBuilder.path("/api/v1/alertas/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark an alert as read")
    public AlertResponse markAsRead(@PathVariable Long id) {
        return alertService.markAsRead(id);
    }

    @PatchMapping("/leidas")
    @Operation(summary = "Mark all user alerts as read")
    public Map<String, Integer> markAllAsRead(@RequestParam UUID userId) {
        return Map.of("actualizadas", alertService.markAllAsRead(userId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar una alert")
    public void delete(@PathVariable Long id) {
        alertService.delete(id);
    }
}
