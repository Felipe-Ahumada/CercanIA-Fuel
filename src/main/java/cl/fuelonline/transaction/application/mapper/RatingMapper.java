package cl.fuelonline.transaction.application.mapper;

import cl.fuelonline.transaction.application.dto.RatingResponse;
import cl.fuelonline.transaction.domain.model.Rating;
import cl.fuelonline.user.domain.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RatingMapper {

    @Mapping(target = "userId",        source = "user.id")
    @Mapping(target = "userName",    expression = "java(nombreUsuario(entity.getUser()))")
    @Mapping(target = "stationId",      source = "station.id")
    @Mapping(target = "stationName",  source = "station.name")
    RatingResponse toResponse(Rating entity);

    default String nombreUsuario(User u) {
        if (u == null) return null;
        return (u.getFirstName() + " " + u.getLastName()).trim();
    }
}
