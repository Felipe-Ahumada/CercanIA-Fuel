package cl.fuelonline.finance.application.dto;

import cl.fuelonline.finance.domain.model.CardType;

public record CardProductResponse(
        Integer id,
        Integer bankId,
        String bankName,
        String name,
        CardType cardType,
        Boolean active
) {}
