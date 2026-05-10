package cl.fuelonline.transaction.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RatingResponse(
        Long id,
        UUID userId,
        String userName,
        UUID stationId,
        String stationName,
        Integer score,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
