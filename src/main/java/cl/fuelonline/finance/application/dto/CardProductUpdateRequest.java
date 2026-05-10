package cl.fuelonline.finance.application.dto;

import cl.fuelonline.finance.domain.model.CardType;
import jakarta.validation.constraints.Size;

public record CardProductUpdateRequest(
        @Size(max = 100) String name,
        CardType cardType
) {}
