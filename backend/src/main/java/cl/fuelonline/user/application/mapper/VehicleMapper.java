package cl.fuelonline.user.application.mapper;

import cl.fuelonline.user.application.dto.VehicleResponse;
import cl.fuelonline.user.domain.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VehicleMapper {

    @Mapping(target = "id",             expression = "java(v.getId().toString())")
    @Mapping(target = "vehicleModelId", source = "model.id")
    @Mapping(target = "brandName",      source = "model.brand.name")
    @Mapping(target = "modelName",      source = "model.name")
    @Mapping(target = "fuelTypeId",     source = "fuelType.id")
    @Mapping(target = "fuelTypeName",   source = "fuelType.name")
    VehicleResponse toResponse(Vehicle v);
}