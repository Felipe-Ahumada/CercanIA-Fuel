package cl.fuelonline.finance.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Mejor discount aplicable y desglose final")
public record CalculatedDiscountResponse(
        @Schema(description = "ID of the applied discount, or null if none applies")
        Integer discountId,
        String description,
        BigDecimal grossAmount,
        BigDecimal discountAmount,
        BigDecimal finalAmount
) {}
