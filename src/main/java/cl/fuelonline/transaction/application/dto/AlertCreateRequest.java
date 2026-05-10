package cl.fuelonline.transaction.application.dto;

import cl.fuelonline.transaction.domain.model.AlertType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.UUID;

public record AlertCreateRequest(

        @NotNull UUID userId,

        @Schema(description = "Station asociada (opcional)")
        UUID stationId,

        @NotNull AlertType alertType,

        @NotBlank @Size(max = 150) String title,

        @NotBlank @Size(max = 500) String message
) {}
