package cl.fuelonline.security.application.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank @Email @Size(max = 180) String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotBlank @Size(max = 12) String rut,
        @NotBlank @Size(max = 80) String firstName,
        @Size(max = 80)           String middleName,
        @NotBlank @Size(max = 80) String lastName,
        @NotBlank @Size(max = 80) String secondLastName,
        @NotNull @Past LocalDate birthDate
) {}
