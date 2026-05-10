package cl.fuelonline.finance.application.dto;

import cl.fuelonline.finance.domain.model.TipoTarjeta;

public record TarjetaProductoResponse(
        Integer id,
        Integer bancoId,
        String bancoNombre,
        String nombre,
        TipoTarjeta tipoTarjeta,
        Boolean activo
) {}
