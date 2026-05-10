package cl.fuelonline.transaction.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FavoriteResponse(
        UUID usuarioId,
        UUID bencineraId,
        String bencineraNombre,
        String bencineraDireccion,
        String marcaNombre,
        BigDecimal latitud,
        BigDecimal longitud,
        String alias,
        LocalDateTime createdAt
) {}
