package cl.fuelonline.transaction.application.mapper;

import cl.fuelonline.transaction.application.dto.FavoriteResponse;
import cl.fuelonline.transaction.domain.model.Favorite;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FavoriteMapper {

    @Mapping(target = "userId",          source = "user.id")
    @Mapping(target = "stationId",        source = "station.id")
    @Mapping(target = "stationName",    source = "station.name")
    @Mapping(target = "stationAddress", source = "station.address")
    @Mapping(target = "brandName",        source = "station.brand.name")
    @Mapping(target = "latitude",            source = "station.latitude")
    @Mapping(target = "longitude",           source = "station.longitude")
    FavoriteResponse toResponse(Favorite entity);
}
