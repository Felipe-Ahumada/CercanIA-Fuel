package cl.fuelonline.transaction.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID usuarioId,
        UUID vehiculoId,
        UUID bencineraId,
        String bencineraNombre,
        Integer tipoCombustibleId,
        String tipoCombustibleNombre,
        Integer tarjetaProductoId,
        String tarjetaProductoNombre,
        Integer descuentoId,
        BigDecimal precioUnitario,
        BigDecimal litros,
        BigDecimal montoBruto,
        BigDecimal montoDescuento,
        BigDecimal montoFinal,
        LocalDateTime fechaTransaccion,
        String observaciones,
        LocalDateTime createdAt
) {}
