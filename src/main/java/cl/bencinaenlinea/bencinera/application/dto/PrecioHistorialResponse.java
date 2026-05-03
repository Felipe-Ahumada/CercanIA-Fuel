package cl.bencinaenlinea.bencinera.application.dto;

import cl.bencinaenlinea.bencinera.domain.model.PrecioHistorial.TipoAtencion;
import cl.bencinaenlinea.bencinera.domain.model.UnidadCobro;

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
