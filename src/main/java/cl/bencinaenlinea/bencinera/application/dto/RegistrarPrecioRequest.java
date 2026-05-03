package cl.bencinaenlinea.bencinera.application.dto;

import cl.bencinaenlinea.bencinera.domain.model.PrecioHistorial.TipoAtencion;
import cl.bencinaenlinea.bencinera.domain.model.UnidadCobro;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RegistrarPrecioRequest(
        @NotNull @Positive Integer tipoCombustibleId,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal precio,
        UnidadCobro unidadCobro,
        TipoAtencion tipoAtencion,
        @NotNull @PastOrPresent LocalDateTime apiTimestamp
) {}
