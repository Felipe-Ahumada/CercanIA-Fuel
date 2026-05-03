package cl.bencinaenlinea.usuario.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Representacion publica de un usuario")
public record UsuarioResponse(
        UUID id,
        String email,
        String rut,
        String primerNombre,
        String segundoNombre,
        String primerApellido,
        String segundoApellido,
        LocalDate fechaNacimiento,
        Integer rolId,
        String rolNombre,
        Boolean activo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
