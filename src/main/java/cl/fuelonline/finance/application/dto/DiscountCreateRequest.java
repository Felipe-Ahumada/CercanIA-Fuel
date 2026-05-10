package cl.fuelonline.finance.application.dto;

import cl.fuelonline.finance.domain.model.DiscountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DiscountCreateRequest(

        @NotNull @Positive Integer brandId,

        @Schema(description = "Null = applies to any payment method")
        @Positive Integer cardProductId,

        @Schema(description = "Null = applies to any fuel")
        @Positive Integer fuelTypeId,

        @Schema(description = "1=Monday, 7=Sunday. Null = applies every day")
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
