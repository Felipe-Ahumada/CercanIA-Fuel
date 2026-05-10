package cl.fuelonline.finance.application.dto;

import jakarta.validation.constraints.Size;

public record BancoUpdateRequest(
        @Size(max = 100) String nombre,
        @Size(max = 20)  String codigo
) {}
