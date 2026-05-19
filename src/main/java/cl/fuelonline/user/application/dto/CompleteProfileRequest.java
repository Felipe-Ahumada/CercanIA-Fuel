package cl.fuelonline.user.application.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CompleteProfileRequest(
        @NotBlank @Email @Size(max = 180) String email,
        @NotBlank @Size(max = 80)         String firstName,
        @Size(max = 80)                   String middleName,
        @NotBlank @Size(max = 80)         String lastName,
        @NotBlank @Size(max = 80)         String secondLastName,
        @NotBlank @Size(max = 12)         String rut,
        @NotNull  @Past                   LocalDate birthDate
) {}