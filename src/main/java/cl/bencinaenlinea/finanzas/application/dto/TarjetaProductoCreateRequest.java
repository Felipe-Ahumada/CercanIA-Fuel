package cl.bencinaenlinea.finanzas.application.dto;

import cl.bencinaenlinea.finanzas.domain.model.TipoTarjeta;
import jakarta.validation.constraints.*;

public record TarjetaProductoCreateRequest(
        @NotNull @Positive       Integer bancoId,
        @NotBlank @Size(max = 100) String nombre,
        @NotNull                 TipoTarjeta tipoTarjeta
) {}
