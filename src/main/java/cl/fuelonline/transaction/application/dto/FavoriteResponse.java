package cl.fuelonline.transaction.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FavoriteResponse(
        UUID userId,
        UUID stationId,
        String stationName,
        String stationAddress,
        String brandName,
        BigDecimal latitude,
        BigDecimal longitude,
        String alias,
        LocalDateTime createdAt
) {}
