package cl.fuelonline.finance.application.dto;

import cl.fuelonline.finance.domain.model.TipoTarjeta;
import jakarta.validation.constraints.Size;

public record TarjetaProductoUpdateRequest(
        @Size(max = 100) String nombre,
        TipoTarjeta tipoTarjeta
) {}
