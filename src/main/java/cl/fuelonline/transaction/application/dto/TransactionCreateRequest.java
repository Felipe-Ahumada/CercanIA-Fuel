package cl.fuelonline.transaction.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionCreateRequest(

        @NotNull UUID userId,

        @Schema(description = "Null if no vehicle registered (rental, anonymous fill).")
        UUID vehicleId,

        @NotNull UUID stationId,

        @NotNull @Positive Integer fuelTypeId,

        @Schema(description = "Card product used. Null if it was cash or another method.")
        @Positive Integer cardProductId,

        @Schema(description = "Applied discount (obtained from /descuentos/calculate). Null if there was none.")
        @Positive Integer discountId,

        @NotNull @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal unitPrice,

        @NotNull @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal liters,

        @Schema(description = "Discount amount. Null is treated as 0.")
        @DecimalMin(value = "0.0")
        BigDecimal discountAmount,

        @Schema(description = "If null, LocalDateTime.now() is used")
        LocalDateTime transactionDate,

        @Size(max = 255) String notes
) {}
