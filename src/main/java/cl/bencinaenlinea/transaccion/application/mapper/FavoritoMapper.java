package cl.bencinaenlinea.transaccion.application.mapper;

import cl.bencinaenlinea.transaccion.application.dto.FavoritoResponse;
import cl.bencinaenlinea.transaccion.domain.model.Favorito;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FavoritoMapper {

    @Mapping(target = "usuarioId",          source = "usuario.id")
    @Mapping(target = "bencineraId",        source = "bencinera.id")
    @Mapping(target = "bencineraNombre",    source = "bencinera.nombre")
    @Mapping(target = "bencineraDireccion", source = "bencinera.direccion")
    @Mapping(target = "marcaNombre",        source = "bencinera.marca.nombre")
    @Mapping(target = "latitud",            source = "bencinera.latitud")
    @Mapping(target = "longitud",           source = "bencinera.longitud")
    FavoritoResponse toResponse(Favorito entity);
}
