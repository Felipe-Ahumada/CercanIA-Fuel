package cl.fuelonline.station.application.dto;

import cl.fuelonline.station.domain.model.PriceHistory.TipoAtencion;
import cl.fuelonline.station.domain.model.ChargeUnit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PriceHistoryResponse(
        Long id,
        BigDecimal price,
        ChargeUnit chargeUnit,
        TipoAtencion attentionType,
        LocalDateTime apiTimestamp,
        LocalDateTime createdAt
) {}
