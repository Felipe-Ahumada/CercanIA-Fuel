package cl.fuelonline.transaction.application.dto;

import java.util.UUID;

public record RatingSummaryResponse(
        UUID bencineraId,
        Double promedio,
        Long total
) {}
