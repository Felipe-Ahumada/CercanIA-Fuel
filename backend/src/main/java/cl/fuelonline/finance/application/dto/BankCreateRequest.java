package cl.fuelonline.finance.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BankCreateRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 20)  String code
) {}
