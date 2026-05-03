package cl.bencinaenlinea.bencinera.application.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record BencineraUpdateRequest(
        @Positive            Integer marcaId,
        @Positive            Integer comunaId,
        @Size(max = 150)     String nombre,
        @Size(max = 255)     String direccion,
        @DecimalMin("-90.0") @DecimalMax("90.0")  BigDecimal latitud,
        @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitud,
        @Size(max = 20)      String telefono,
        @Email @Size(max = 120) String email,
        Boolean              enMantenimiento
) {}
