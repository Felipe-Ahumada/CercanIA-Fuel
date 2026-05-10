package cl.fuelonline.transaction.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ResumenGastoResponse(
        LocalDate desde,
        LocalDate hasta,
        BigDecimal totalGastado,
        BigDecimal totalAhorrado,
        BigDecimal totalLitros,
        long cantidadCargas
) {}
