package cl.fuelonline.user.application.dto;

import jakarta.validation.constraints.*;

public record VehicleCreateRequest(
        @NotNull Integer vehicleModelId,
        @NotNull Integer fuelTypeId,
        @NotBlank @Size(max = 10) String licensePlate,
        @NotNull @Min(1900) @Max(2030) Integer year
) {}
