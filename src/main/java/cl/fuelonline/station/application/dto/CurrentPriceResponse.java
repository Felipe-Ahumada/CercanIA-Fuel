package cl.fuelonline.station.application.dto;

import cl.fuelonline.station.domain.model.PriceHistory.TipoAtencion;
import cl.fuelonline.station.domain.model.ChargeUnit;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Precio vigente de un combustible en una bencinera")
public record CurrentPriceResponse(
        Integer tipoCombustibleId,
        String tipoCombustibleNombre,
        BigDecimal precio,
        ChargeUnit unidadCobro,
        TipoAtencion tipoAtencion,
        LocalDateTime apiTimestamp
) {}
