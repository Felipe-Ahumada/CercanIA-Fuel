package cl.bencinaenlinea.transaccion.application.mapper;

import cl.bencinaenlinea.transaccion.application.dto.AlertaResponse;
import cl.bencinaenlinea.transaccion.domain.model.Alerta;
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
