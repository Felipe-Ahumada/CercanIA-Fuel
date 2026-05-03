package cl.bencinaenlinea.usuario.application.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UsuarioUpdateRequest(
        @Email @Size(max = 180) String email,
        @Size(max = 80)         String primerNombre,
        @Size(max = 80)         String segundoNombre,
        @Size(max = 80)         String primerApellido,
        @Size(max = 80)         String segundoApellido,
        @Past                   LocalDate fechaNacimiento,
        @Positive               Integer rolId
) {}
