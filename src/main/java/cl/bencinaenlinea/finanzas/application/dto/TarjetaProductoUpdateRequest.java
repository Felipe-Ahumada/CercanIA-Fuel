package cl.bencinaenlinea.finanzas.application.dto;

import cl.bencinaenlinea.finanzas.domain.model.TipoTarjeta;
import jakarta.validation.constraints.Size;

public record TarjetaProductoUpdateRequest(
        @Size(max = 100) String nombre,
        TipoTarjeta tipoTarjeta
) {}
