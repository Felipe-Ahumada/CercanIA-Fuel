package cl.fuelonline.station.integration.cne.dto;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Metricas de un sync CNE. Lo devuelve el endpoint manual y se loguea desde el scheduler.
 */
public record CneSyncResultadoDto(
        LocalDateTime inicio,
        long          duracionMs,
        int           estacionesLeidas,
        int           bencinerasCreadas,
        int           bencinerasActualizadas,
        int           preciosInsertados,
        int           preciosOmitidos,
        int           errores
) {
    public static CneSyncResultadoDto vacio(LocalDateTime inicio, Duration duracion, String motivo) {
        return new CneSyncResultadoDto(inicio, duracion.toMillis(),
                0, 0, 0, 0, 0, 0);
    }
}
