package cl.fuelonline.station.application.dto;

import cl.fuelonline.station.domain.model.PriceHistory.AttentionType;
import cl.fuelonline.station.domain.model.ChargeUnit;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PriceRegisterRequest(
        @NotNull @Positive Integer fuelTypeId,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
        ChargeUnit chargeUnit,
        AttentionType attentionType,
        @NotNull @PastOrPresent LocalDateTime apiTimestamp
) {}
