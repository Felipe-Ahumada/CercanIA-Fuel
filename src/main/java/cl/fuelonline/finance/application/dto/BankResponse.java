package cl.fuelonline.finance.application.dto;

public record BankResponse(
        Integer id,
        String nombre,
        String codigo,
        Boolean activo
) {}
