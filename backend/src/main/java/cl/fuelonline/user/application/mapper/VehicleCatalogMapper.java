package cl.fuelonline.user.application.mapper;

import cl.fuelonline.user.application.dto.VehicleBrandResponse;
import cl.fuelonline.user.application.dto.VehicleModelResponse;
import cl.fuelonline.user.domain.model.VehicleBrand;
import cl.fuelonline.user.domain.model.VehicleModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VehicleCatalogMapper {

    VehicleBrandResponse toBrandResponse(VehicleBrand brand);

    @Mapping(target = "brandId",     source = "brand.id")
    @Mapping(target = "brandName",   source = "brand.name")
    @Mapping(target = "vehicleType", expression = "java(model.getVehicleType().name())")
    VehicleModelResponse toModelResponse(VehicleModel model);
}