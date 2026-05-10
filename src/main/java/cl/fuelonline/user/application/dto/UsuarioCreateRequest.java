package cl.fuelonline.user.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(description = "Datos para crear un usuario")
public record UsuarioCreateRequest(

        @Schema(example = "rolando.lopez@correo.cl")
        @NotBlank @Email @Size(max = 180) String email,

        @Schema(example = "12.345.678-9", description = "RUT chileno con o sin formato")
        @NotBlank @Size(max = 12) String rut,

        @NotBlank @Size(max = 80) String primerNombre,
        @Size(max = 80)           String segundoNombre,
        @NotBlank @Size(max = 80) String primerApellido,
        @NotBlank @Size(max = 80) String segundoApellido,

        @NotNull @Past LocalDate fechaNacimiento,

        @Schema(example = "2", description = "ID del Rol asignado")
        @NotNull @Positive Integer rolId
) {}
