package cl.fuelonline.station.application.dto;

import cl.fuelonline.station.domain.model.PriceHistory.TipoAtencion;
import cl.fuelonline.station.domain.model.ChargeUnit;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RegistrarPrecioRequest(
        @NotNull @Positive Integer tipoCombustibleId,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal precio,
        ChargeUnit unidadCobro,
        TipoAtencion tipoAtencion,
        @NotNull @PastOrPresent LocalDateTime apiTimestamp
) {}
