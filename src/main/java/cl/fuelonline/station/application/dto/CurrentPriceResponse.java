package cl.fuelonline.station.application.dto;

import cl.fuelonline.station.domain.model.PriceHistory.TipoAtencion;
import cl.fuelonline.station.domain.model.ChargeUnit;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Current price of a fuel at a station")
public record CurrentPriceResponse(
        Integer fuelTypeId,
        String fuelTypeName,
        BigDecimal price,
        ChargeUnit chargeUnit,
        TipoAtencion attentionType,
        LocalDateTime apiTimestamp
) {}
