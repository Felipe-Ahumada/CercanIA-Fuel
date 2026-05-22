package cl.fuelonline.user.application.dto;

public record AdminUserResponse(
        String  id,
        String  email,
        String  rut,
        String  firstName,
        String  middleName,
        String  lastName,
        String  secondLastName,
        String  birthDate,
        Integer roleId,
        String  roleName,
        boolean active,
        String  createdAt,
        String  updatedAt,
        long    vehicleCount,
        long    totalTransactions,
        double  totalSavings
) {}
