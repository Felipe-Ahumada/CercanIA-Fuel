package cl.fuelonline.transaction.application.dto;

import cl.fuelonline.transaction.domain.model.AlertType;

import java.time.LocalDateTime;
import java.util.UUID;

public record AlertResponse(
        Long id,
        UUID userId,
        UUID stationId,
        String stationName,
        AlertType alertType,
        String title,
        String message,
        Boolean read,
        LocalDateTime readAt,
        LocalDateTime createdAt
) {}
