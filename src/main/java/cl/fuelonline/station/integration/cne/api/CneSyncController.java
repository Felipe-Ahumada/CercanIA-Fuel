package cl.fuelonline.station.integration.cne.api;

import cl.fuelonline.station.integration.cne.dto.CneSyncResultDto;
import cl.fuelonline.station.integration.cne.service.CneSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/cne")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.cne", name = "enabled", havingValue = "true")
@Tag(name = "Admin / CNE", description = "Administrative operations on the CNE integration")
public class CneSyncController {

    private final CneSyncService syncService;

    @PostMapping("/sync")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Triggers a manual sync against the CNE API",
               description = "Solo role ADMIN. Devuelve metricas del sync.",
               security = @SecurityRequirement(name = "bearerAuth"))
    public CneSyncResultDto sync() {
        return syncService.synchronize();
    }
}
