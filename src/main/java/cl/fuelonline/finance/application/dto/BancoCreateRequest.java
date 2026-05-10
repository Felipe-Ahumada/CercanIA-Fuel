package cl.fuelonline.finance.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BancoCreateRequest(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Size(max = 20)  String codigo
) {}
