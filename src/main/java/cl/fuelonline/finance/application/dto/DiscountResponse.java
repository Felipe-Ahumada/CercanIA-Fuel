package cl.fuelonline.finance.application.dto;

import cl.fuelonline.finance.domain.model.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DiscountResponse(
        Integer id,
        Integer marcaId,
        String marcaNombre,
        Integer tarjetaProductoId,
        String tarjetaProductoNombre,
        Integer tipoCombustibleId,
        String tipoCombustibleNombre,
        Integer diaSemana,
        DiscountType tipoDescuento,
        BigDecimal valorDescuento,
        BigDecimal topeMaximo,
        String descripcion,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Boolean activo,
        LocalDateTime createdAt
) {}
