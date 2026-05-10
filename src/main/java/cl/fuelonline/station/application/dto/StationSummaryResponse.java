package cl.fuelonline.station.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Vista compacta de station para listados y mapa")
public record StationSummaryResponse(
        UUID id,
        String name,
        String brand,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        Boolean inMaintenance,
        @Schema(description = "Distancia al punto de busqueda en km. Null si la consulta no especifico origen.")
        Double distanciaKm
) {}
