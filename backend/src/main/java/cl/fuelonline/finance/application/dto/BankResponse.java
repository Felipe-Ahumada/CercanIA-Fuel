package cl.fuelonline.finance.application.dto;

public record BankResponse(
        Integer id,
        String name,
        String code,
        Boolean active
) {}
