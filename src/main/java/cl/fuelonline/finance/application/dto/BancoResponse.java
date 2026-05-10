package cl.fuelonline.finance.application.dto;

public record BancoResponse(
        Integer id,
        String nombre,
        String codigo,
        Boolean activo
) {}
