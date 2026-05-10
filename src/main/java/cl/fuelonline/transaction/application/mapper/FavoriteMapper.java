package cl.fuelonline.transaction.application.mapper;

import cl.fuelonline.transaction.application.dto.FavoriteResponse;
import cl.fuelonline.transaction.domain.model.Favorite;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FavoriteMapper {

    @Mapping(target = "usuarioId",          source = "usuario.id")
    @Mapping(target = "bencineraId",        source = "bencinera.id")
    @Mapping(target = "bencineraNombre",    source = "bencinera.nombre")
    @Mapping(target = "bencineraDireccion", source = "bencinera.direccion")
    @Mapping(target = "marcaNombre",        source = "bencinera.marca.nombre")
    @Mapping(target = "latitud",            source = "bencinera.latitud")
    @Mapping(target = "longitud",           source = "bencinera.longitud")
    FavoriteResponse toResponse(Favorite entity);
}
