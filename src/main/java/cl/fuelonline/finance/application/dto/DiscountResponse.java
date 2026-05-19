package cl.fuelonline.finance.application.dto;

import cl.fuelonline.finance.domain.model.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DiscountResponse(
        Integer id,
        Integer brandId,
        String brandName,
        Integer cardProductId,
        String cardProductName,
        String bankName,
        Integer fuelTypeId,
        String fuelTypeName,
        Integer dayOfWeek,
        DiscountType discountType,
        BigDecimal discountValue,
        BigDecimal maxCap,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        Boolean active,
        LocalDateTime createdAt
) {}
