package cl.fuelonline.finance.application.dto;

import cl.fuelonline.finance.domain.model.CardType;

public record CardProductResponse(
        Integer id,
        Integer bancoId,
        String bancoNombre,
        String nombre,
        CardType tipoTarjeta,
        Boolean activo
) {}
