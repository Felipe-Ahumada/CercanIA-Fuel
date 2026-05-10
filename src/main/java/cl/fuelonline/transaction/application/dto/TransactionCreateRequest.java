package cl.fuelonline.transaction.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionCreateRequest(

        @NotNull UUID userId,

        @NotNull UUID vehicleId,

        @NotNull UUID stationId,

        @NotNull @Positive Integer fuelTypeId,

        @Schema(description = "Producto de tarjeta usado. Null si fue efectivo u otro medio.")
        @Positive Integer cardProductId,

        @Schema(description = "Discount aplicado (obtenido desde /descuentos/calculate). Null si no hubo.")
        @Positive Integer discountId,

        @NotNull @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal unitPrice,

        @NotNull @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal liters,

        @Schema(description = "Monto descontado. Null se interpreta como 0.")
        @DecimalMin(value = "0.0")
        BigDecimal discountAmount,

        @Schema(description = "Si es null se usa LocalDateTime.now()")
        LocalDateTime transactionDate,

        @Size(max = 255) String notes
) {}
