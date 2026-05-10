package cl.fuelonline.station.application.dto;

import cl.fuelonline.station.domain.model.PrecioHistorial.TipoAtencion;
import cl.fuelonline.station.domain.model.UnidadCobro;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PrecioHistorialResponse(
        Long id,
        BigDecimal precio,
        UnidadCobro unidadCobro,
        TipoAtencion tipoAtencion,
        LocalDateTime apiTimestamp,
        LocalDateTime createdAt
) {}
