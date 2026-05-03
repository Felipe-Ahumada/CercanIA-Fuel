package cl.bencinaenlinea.bencinera.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Datos para crear una bencinera")
public record BencineraCreateRequest(

        @Schema(example = "CL_COP_001", description = "Codigo externo (API CNE u otro)")
        @NotBlank @Size(max = 30) String codigoApi,

        @NotNull @Positive Integer marcaId,
        @NotNull @Positive Integer comunaId,

        @NotBlank @Size(max = 150) String nombre,
        @NotBlank @Size(max = 255) String direccion,

        @Schema(example = "-33.4569400")
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitud,

        @Schema(example = "-70.6482700")
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitud,

        @Size(max = 20)  String telefono,
        @Email @Size(max = 120) String email
) {}
