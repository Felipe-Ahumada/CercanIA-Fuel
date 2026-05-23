package cl.fuelonline.user.application.dto;

public record VehicleResponse(
        String id,
        Integer vehicleModelId,
        String brandName,
        String modelName,
        Integer fuelTypeId,
        String fuelTypeName,
        String licensePlate,
        Integer year
) {}
