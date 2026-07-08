package cl.fuelonline.user.application.dto;

import jakarta.validation.constraints.*;

public record VehicleCreateRequest(
        @NotNull Integer vehicleModelId,
        @NotNull Integer fuelTypeId,
        @NotBlank
        @Pattern(
            regexp = "^[A-Z]{4}[0-9]{2}$|^[A-Z]{2}[0-9]{4}$|^[A-Z]{2}[0-9]{2}[A-Z]$",
            message = "Formato de patente inválido (ej: ABCD12, AB1234, AB12C)"
        )
        String licensePlate,
        @NotNull @Min(1900) @Max(2030) Integer year
) {}
