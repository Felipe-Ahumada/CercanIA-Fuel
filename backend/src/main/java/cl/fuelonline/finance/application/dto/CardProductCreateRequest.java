package cl.fuelonline.finance.application.dto;

import cl.fuelonline.finance.domain.model.CardType;
import jakarta.validation.constraints.*;

public record CardProductCreateRequest(
        @NotNull @Positive       Integer bankId,
        @NotBlank @Size(max = 100) String name,
        @NotNull                 CardType cardType
) {}
