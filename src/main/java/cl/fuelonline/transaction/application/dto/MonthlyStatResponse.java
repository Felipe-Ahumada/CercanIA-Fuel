package cl.fuelonline.transaction.application.dto;

import java.math.BigDecimal;

public record MonthlyStatResponse(
        String month,
        BigDecimal totalSaved,
        BigDecimal totalLiters
) {}
