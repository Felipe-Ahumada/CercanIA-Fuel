package cl.fuelonline.user.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(description = "Datos para create un user")
public record UserCreateRequest(

        @Schema(example = "rolando.lopez@correo.cl")
        @NotBlank @Email @Size(max = 180) String email,

        @Schema(example = "12.345.678-9", description = "RUT chileno con o sin formato")
        @NotBlank @Size(max = 12) String rut,

        @NotBlank @Size(max = 80) String firstName,
        @Size(max = 80)           String middleName,
        @NotBlank @Size(max = 80) String lastName,
        @NotBlank @Size(max = 80) String secondLastName,

        @NotNull @Past LocalDate birthDate,

        @Schema(example = "2", description = "ID del Role asignado")
        @NotNull @Positive Integer roleId
) {}
