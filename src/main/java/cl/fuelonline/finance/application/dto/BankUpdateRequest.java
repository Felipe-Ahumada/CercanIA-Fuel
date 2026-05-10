package cl.fuelonline.finance.application.dto;

import jakarta.validation.constraints.Size;

public record BankUpdateRequest(
        @Size(max = 100) String name,
        @Size(max = 20)  String code
) {}
