package cl.bencinaenlinea.transaccion.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FavoritoResponse(
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
