package cl.fuelonline.admin.api;

import cl.fuelonline.admin.application.dto.AnalyticsResponse;
import cl.fuelonline.admin.application.service.AdminAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Analytics", description = "Métricas agregadas para el panel de administración")
public class AdminAnalyticsController {

    private final AdminAnalyticsService analyticsService;

    @GetMapping("/analytics")
    @Operation(summary = "Devuelve todas las métricas del dashboard admin")
    public AnalyticsResponse analytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer regionId
    ) {
        return analyticsService.getAnalytics(startDate, endDate, regionId);
    }
}
