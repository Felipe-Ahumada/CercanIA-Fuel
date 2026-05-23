package cl.fuelonline.station.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Data to create a station")
public record StationCreateRequest(

        @Schema(example = "CL_COP_001", description = "Codigo externo (API CNE u otro)")
        @NotBlank @Size(max = 30) String apiCode,

        @NotNull @Positive Integer brandId,
        @NotNull @Positive Integer communeId,

        @NotBlank @Size(max = 150) String name,
        @NotBlank @Size(max = 255) String address,

        @Schema(example = "-33.4569400")
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,

        @Schema(example = "-70.6482700")
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,

        @Size(max = 20)  String phone,
        @Email @Size(max = 120) String email
) {}
