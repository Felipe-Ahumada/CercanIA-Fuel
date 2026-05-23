package cl.fuelonline.user.application.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserUpdateRequest(
        @Email @Size(max = 254) String email,
        @Size(max = 80)         String firstName,
        @Size(max = 80)         String middleName,
        @Size(max = 80)         String lastName,
        @Size(max = 80)         String secondLastName,
        @Past                   LocalDate birthDate,
        @Positive               Integer roleId,
        Boolean active
) {}
