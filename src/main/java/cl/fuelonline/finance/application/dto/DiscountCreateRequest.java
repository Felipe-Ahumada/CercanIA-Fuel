package cl.fuelonline.finance.application.dto;

import cl.fuelonline.finance.domain.model.DiscountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DiscountCreateRequest(

        @NotNull @Positive Integer marcaId,

        @Schema(description = "Null = aplica a cualquier medio de pago")
        @Positive Integer tarjetaProductoId,

        @Schema(description = "Null = aplica a cualquier combustible")
        @Positive Integer tipoCombustibleId,

        @Schema(description = "1=lunes, 7=domingo. Null = aplica todos los dias")
        @Min(1) @Max(7) Integer diaSemana,

        @NotNull DiscountType tipoDescuento,

        @NotNull @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal valorDescuento,

        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal topeMaximo,

        @Size(max = 255) String descripcion,

        @NotNull LocalDate fechaInicio,
        LocalDate fechaFin
) {}
