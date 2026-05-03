package cl.bencinaenlinea.usuario.application.mapper;

import cl.bencinaenlinea.usuario.application.dto.UsuarioCreateRequest;
import cl.bencinaenlinea.usuario.application.dto.UsuarioResponse;
import cl.bencinaenlinea.usuario.application.dto.UsuarioUpdateRequest;
import cl.bencinaenlinea.usuario.domain.model.Usuario;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsuarioMapper {

    @Mapping(target = "id",         ignore = true)
    @Mapping(target = "rol",        ignore = true)
    @Mapping(target = "vehiculos",  ignore = true)
    @Mapping(target = "activo",     constant = "true")
    Usuario toEntity(UsuarioCreateRequest req);

    @Mapping(target = "rolId",     source = "rol.id")
    @Mapping(target = "rolNombre", source = "rol.nombre")
    UsuarioResponse toResponse(Usuario u);

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "rol",       ignore = true)
    @Mapping(target = "vehiculos", ignore = true)
    @Mapping(target = "activo",    ignore = true)
    @Mapping(target = "rut",       ignore = true)
    void updateEntity(UsuarioUpdateRequest req, @MappingTarget Usuario entity);
}
