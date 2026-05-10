package cl.fuelonline.transaction.application.mapper;

import cl.fuelonline.transaction.application.dto.AlertResponse;
import cl.fuelonline.transaction.domain.model.Alert;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlertMapper {

    @Mapping(target = "usuarioId",        source = "usuario.id")
    @Mapping(target = "bencineraId",      source = "bencinera.id")
    @Mapping(target = "bencineraNombre",  source = "bencinera.nombre")
    AlertResponse toResponse(Alert entity);
}
