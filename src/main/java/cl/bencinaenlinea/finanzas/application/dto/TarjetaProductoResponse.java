package cl.bencinaenlinea.finanzas.application.dto;

import cl.bencinaenlinea.finanzas.domain.model.TipoTarjeta;

public record TarjetaProductoResponse(
        Integer id,
        Integer bancoId,
        String bancoNombre,
        String nombre,
        TipoTarjeta tipoTarjeta,
        Boolean activo
) {}
