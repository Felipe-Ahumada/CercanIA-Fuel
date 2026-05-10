package cl.fuelonline.transaction.application.mapper;

import cl.fuelonline.transaction.application.dto.AlertaResponse;
import cl.fuelonline.transaction.domain.model.Alerta;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlertaMapper {

    @Mapping(target = "usuarioId",        source = "usuario.id")
    @Mapping(target = "bencineraId",      source = "bencinera.id")
    @Mapping(target = "bencineraNombre",  source = "bencinera.nombre")
    AlertaResponse toResponse(Alerta entity);
}
