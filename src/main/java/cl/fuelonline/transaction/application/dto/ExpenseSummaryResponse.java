package cl.fuelonline.transaction.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ExpenseSummaryResponse(
        LocalDate desde,
        LocalDate hasta,
        BigDecimal totalSpent,
        BigDecimal totalSaved,
        BigDecimal totalLiters,
        long fillCount,
        List<MonthlyStatResponse> byMonth
) {}
