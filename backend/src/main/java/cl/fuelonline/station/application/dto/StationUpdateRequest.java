package cl.fuelonline.station.application.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record StationUpdateRequest(
        @Positive            Integer brandId,
        @Positive            Integer communeId,
        @Size(max = 150)     String name,
        @Size(max = 255)     String address,
        @DecimalMin("-90.0") @DecimalMax("90.0")  BigDecimal latitude,
        @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,
        @Size(max = 20)      String phone,
        @Email @Size(max = 120) String email,
        Boolean              inMaintenance
) {}
