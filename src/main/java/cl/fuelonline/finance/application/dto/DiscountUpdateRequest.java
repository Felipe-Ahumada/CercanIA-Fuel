package cl.fuelonline.finance.application.dto;

import cl.fuelonline.finance.domain.model.DiscountType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DiscountUpdateRequest(
        @Positive Integer cardProductId,
        @Positive Integer fuelTypeId,
        @Min(1) @Max(7) Integer dayOfWeek,
        DiscountType discountType,
        @DecimalMin(value = "0.0", inclusive = false) BigDecimal discountValue,
        @DecimalMin(value = "0.0", inclusive = false) BigDecimal maxCap,
        @Size(max = 255) String description,
        LocalDate startDate,
        LocalDate endDate,
        Boolean active
) {}
