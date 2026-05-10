package cl.fuelonline.finance.application.dto;

import cl.fuelonline.finance.domain.model.DiscountType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DiscountUpdateRequest(
        @Positive Integer tarjetaProductoId,
        @Positive Integer tipoCombustibleId,
        @Min(1) @Max(7) Integer diaSemana,
        DiscountType tipoDescuento,
        @DecimalMin(value = "0.0", inclusive = false) BigDecimal valorDescuento,
        @DecimalMin(value = "0.0", inclusive = false) BigDecimal topeMaximo,
        @Size(max = 255) String descripcion,
        LocalDate fechaInicio,
        LocalDate fechaFin
) {}
