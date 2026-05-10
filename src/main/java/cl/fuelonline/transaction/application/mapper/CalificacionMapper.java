package cl.fuelonline.transaction.application.mapper;

import cl.fuelonline.transaction.application.dto.CalificacionResponse;
import cl.fuelonline.transaction.domain.model.Calificacion;
import cl.fuelonline.user.domain.model.Usuario;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CalificacionMapper {

    @Mapping(target = "usuarioId",        source = "usuario.id")
    @Mapping(target = "usuarioNombre",    expression = "java(nombreUsuario(entity.getUsuario()))")
    @Mapping(target = "bencineraId",      source = "bencinera.id")
    @Mapping(target = "bencineraNombre",  source = "bencinera.nombre")
    CalificacionResponse toResponse(Calificacion entity);

    default String nombreUsuario(Usuario u) {
        if (u == null) return null;
        return (u.getPrimerNombre() + " " + u.getPrimerApellido()).trim();
    }
}
