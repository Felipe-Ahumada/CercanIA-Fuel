package cl.fuelonline.station.application.dto;

import cl.fuelonline.station.domain.model.PrecioHistorial.TipoAtencion;
import cl.fuelonline.station.domain.model.UnidadCobro;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Precio vigente de un combustible en una bencinera")
public record PrecioActualResponse(
        Integer tipoCombustibleId,
        String tipoCombustibleNombre,
        BigDecimal precio,
        UnidadCobro unidadCobro,
        TipoAtencion tipoAtencion,
        LocalDateTime apiTimestamp
) {}
