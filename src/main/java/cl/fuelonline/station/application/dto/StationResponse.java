package cl.fuelonline.station.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Vista detallada de una bencinera")
public record StationResponse(
        UUID id,
        String codigoApi,
        Integer marcaId,
        String marcaNombre,
        Integer comunaId,
        String comunaNombre,
        Integer regionId,
        String nombre,
        String direccion,
        BigDecimal latitud,
        BigDecimal longitud,
        String telefono,
        String email,
        Boolean enMantenimiento,
        Boolean activo,
        LocalDateTime syncAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<CurrentPriceResponse> precios
) {}
