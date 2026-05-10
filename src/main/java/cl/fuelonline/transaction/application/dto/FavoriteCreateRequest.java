package cl.fuelonline.transaction.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record FavoriteCreateRequest(

        @NotNull UUID userId,

        @NotNull UUID stationId,

        @Size(max = 80) String alias
) {}
