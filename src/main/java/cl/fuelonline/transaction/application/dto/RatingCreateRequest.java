package cl.fuelonline.transaction.application.dto;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record RatingCreateRequest(

        @NotNull UUID userId,

        @NotNull UUID stationId,

        @NotNull @Min(1) @Max(5) Integer score,

        @Size(max = 500) String comment
) {}
