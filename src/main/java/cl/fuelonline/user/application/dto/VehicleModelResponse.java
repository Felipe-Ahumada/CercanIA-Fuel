package cl.fuelonline.user.application.dto;

public record VehicleModelResponse(
        Integer id,
        Integer brandId,
        String brandName,
        String name,
        String vehicleType
) {}
