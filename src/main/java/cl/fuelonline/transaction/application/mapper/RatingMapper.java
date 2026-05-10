package cl.fuelonline.transaction.application.mapper;

import cl.fuelonline.transaction.application.dto.RatingResponse;
import cl.fuelonline.transaction.domain.model.Rating;
import cl.fuelonline.user.domain.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RatingMapper {

    @Mapping(target = "usuarioId",        source = "usuario.id")
    @Mapping(target = "usuarioNombre",    expression = "java(nombreUsuario(entity.getUsuario()))")
    @Mapping(target = "bencineraId",      source = "bencinera.id")
    @Mapping(target = "bencineraNombre",  source = "bencinera.nombre")
    RatingResponse toResponse(Rating entity);

    default String nombreUsuario(User u) {
        if (u == null) return null;
        return (u.getPrimerNombre() + " " + u.getPrimerApellido()).trim();
    }
}
