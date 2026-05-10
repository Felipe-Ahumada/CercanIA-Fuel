package cl.fuelonline.finance.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Mejor discount aplicable y desglose final")
public record CalculatedDiscountResponse(
        @Schema(description = "ID del discount aplicado, o null si ninguno aplica")
        Integer discountId,
        String description,
        BigDecimal grossAmount,
        BigDecimal discountAmount,
        BigDecimal finalAmount
) {}
