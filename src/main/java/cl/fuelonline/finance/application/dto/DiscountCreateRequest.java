package cl.fuelonline.finance.application.dto;

import cl.fuelonline.finance.domain.model.DiscountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DiscountCreateRequest(

        @NotNull @Positive Integer brandId,

        @Schema(description = "Null = aplica a cualquier medio de pago")
        @Positive Integer cardProductId,

        @Schema(description = "Null = aplica a cualquier combustible")
        @Positive Integer fuelTypeId,

        @Schema(description = "1=lunes, 7=domingo. Null = aplica todos los dias")
        @Min(1) @Max(7) Integer dayOfWeek,

        @NotNull DiscountType discountType,

        @NotNull @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal discountValue,

        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal maxCap,

        @Size(max = 255) String description,

        @NotNull LocalDate startDate,
        LocalDate endDate
) {}
