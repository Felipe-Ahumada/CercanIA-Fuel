package cl.fuelonline.transaction.application.mapper;

import cl.fuelonline.transaction.application.dto.AlertResponse;
import cl.fuelonline.transaction.domain.model.Alert;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlertMapper {

    @Mapping(target = "userId",        source = "user.id")
    @Mapping(target = "stationId",      source = "station.id")
    @Mapping(target = "stationName",  source = "station.name")
    AlertResponse toResponse(Alert entity);
}
