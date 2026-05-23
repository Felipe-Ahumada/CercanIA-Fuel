package cl.fuelonline.chat.application.dto;

import java.time.Instant;

public record ChatResponse(
        String id,
        String role,
        String text,
        Instant sentAt
) {}
