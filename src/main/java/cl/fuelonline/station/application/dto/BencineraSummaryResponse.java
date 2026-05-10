package cl.fuelonline.station.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Vista compacta de bencinera para listados y mapa")
public record BencineraSummaryResponse(
        UUID id,
        String nombre,
        String marca,
        String direccion,
        BigDecimal latitud,
        BigDecimal longitud,
        Boolean enMantenimiento,
        @Schema(description = "Distancia al punto de busqueda en km. Null si la consulta no especifico origen.")
        Double distanciaKm
) {}
