package cl.bencinaenlinea.finanzas.application.dto;

public record BancoResponse(
        Integer id,
        String nombre,
        String codigo,
        Boolean activo
) {}
