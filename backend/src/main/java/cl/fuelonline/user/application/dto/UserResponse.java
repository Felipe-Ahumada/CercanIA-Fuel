package cl.fuelonline.user.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Representacion publica de un user")
public record UserResponse(
        UUID id,
        String email,
        String rut,
        String firstName,
        String middleName,
        String lastName,
        String secondLastName,
        LocalDate birthDate,
        Integer roleId,
        String roleName,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
