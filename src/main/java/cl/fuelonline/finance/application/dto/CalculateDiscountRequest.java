package cl.fuelonline.finance.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Solicitud para calculate el mejor discount aplicable a una compra")
public record CalculateDiscountRequest(

        @NotNull @Positive Integer brandId,
        @NotNull @Positive Integer fuelTypeId,

        @Schema(description = "Monto bruto de la compra (sin discount)", example = "25000")
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal grossAmount,

        @Schema(description = "IDs de tarjetas-producto que tiene el user. Vacio = sin tarjetas")
        List<@Positive Integer> userCardIds,

        @Schema(description = "Fecha de la compra. Si es null, se usa la date de hoy.")
        LocalDate date
) {}
