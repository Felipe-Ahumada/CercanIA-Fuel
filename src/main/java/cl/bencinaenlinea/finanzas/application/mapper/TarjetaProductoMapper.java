package cl.bencinaenlinea.finanzas.application.mapper;

import cl.bencinaenlinea.finanzas.application.dto.TarjetaProductoCreateRequest;
import cl.bencinaenlinea.finanzas.application.dto.TarjetaProductoResponse;
import cl.bencinaenlinea.finanzas.application.dto.TarjetaProductoUpdateRequest;
import cl.bencinaenlinea.finanzas.domain.model.TarjetaProducto;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TarjetaProductoMapper {

    @Mapping(target = "id",     ignore = true)
    @Mapping(target = "banco",  ignore = true)
    @Mapping(target = "activo", constant = "true")
    TarjetaProducto toEntity(TarjetaProductoCreateRequest req);

    @Mapping(target = "bancoId",     source = "banco.id")
    @Mapping(target = "bancoNombre", source = "banco.nombre")
    TarjetaProductoResponse toResponse(TarjetaProducto entity);

    @Mapping(target = "id",     ignore = true)
    @Mapping(target = "banco",  ignore = true)
    @Mapping(target = "activo", ignore = true)
    void updateEntity(TarjetaProductoUpdateRequest req, @MappingTarget TarjetaProducto entity);
}
