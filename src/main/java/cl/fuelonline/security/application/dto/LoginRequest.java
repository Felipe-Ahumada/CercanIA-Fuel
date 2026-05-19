package cl.fuelonline.security.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank @Email @Size(max = 180) String email,
        @NotBlank @Size(min = 8, max = 100) String password
) {}
