package cl.fuelonline.transaction.application.dto;

import java.util.UUID;

public record RatingSummaryResponse(
        UUID stationId,
        Double average,
        Long total
) {}
