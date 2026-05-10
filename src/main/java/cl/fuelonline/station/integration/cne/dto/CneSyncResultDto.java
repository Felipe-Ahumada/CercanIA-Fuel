package cl.fuelonline.station.integration.cne.dto;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Metricas de un sync CNE. Lo devuelve el endpoint manual y se loguea desde el scheduler.
 */
public record CneSyncResultDto(
        LocalDateTime start,
        long          durationMs,
        int           stationsRead,
        int           stationsCreated,
        int           stationsUpdated,
        int           pricesInserted,
        int           pricesSkipped,
        int           errors
) {
    public static CneSyncResultDto empty(LocalDateTime start, Duration duration, String motivo) {
        return new CneSyncResultDto(start, duration.toMillis(),
                0, 0, 0, 0, 0, 0);
    }
}
