package cl.fuelonline.transaction.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionCreateRequest(

        @NotNull UUID usuarioId,

        @NotNull UUID vehiculoId,

        @NotNull UUID bencineraId,

        @NotNull @Positive Integer tipoCombustibleId,

        @Schema(description = "Producto de tarjeta usado. Null si fue efectivo u otro medio.")
        @Positive Integer tarjetaProductoId,

        @Schema(description = "Discount aplicado (obtenido desde /descuentos/calcular). Null si no hubo.")
        @Positive Integer descuentoId,

        @NotNull @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal precioUnitario,

        @NotNull @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal litros,

        @Schema(description = "Monto descontado. Null se interpreta como 0.")
        @DecimalMin(value = "0.0")
        BigDecimal montoDescuento,

        @Schema(description = "Si es null se usa LocalDateTime.now()")
        LocalDateTime fechaTransaccion,

        @Size(max = 255) String observaciones
) {}
