package cl.fuelonline.transaction.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID userId,
        UUID vehicleId,
        UUID stationId,
        String stationName,
        String stationBrand,
        Integer fuelTypeId,
        String fuelTypeName,
        Integer cardProductId,
        String cardProductName,
        Integer discountId,
        BigDecimal unitPrice,
        BigDecimal liters,
        BigDecimal grossAmount,
        BigDecimal discountAmount,
        BigDecimal finalAmount,
        LocalDateTime transactionDate,
        String notes,
        LocalDateTime createdAt
) {}
