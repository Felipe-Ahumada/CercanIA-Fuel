package cl.fuelonline.finance.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Mejor descuento aplicable y desglose final")
public record CalculatedDiscountResponse(
        @Schema(description = "ID del descuento aplicado, o null si ninguno aplica")
        Integer descuentoId,
        String descripcion,
        BigDecimal montoBruto,
        BigDecimal montoDescuento,
        BigDecimal montoFinal
) {}
