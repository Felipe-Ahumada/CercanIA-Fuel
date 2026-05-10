package cl.fuelonline.finance.application.mapper;

import cl.fuelonline.finance.application.dto.DescuentoCreateRequest;
import cl.fuelonline.finance.application.dto.DescuentoResponse;
import cl.fuelonline.finance.application.dto.DescuentoUpdateRequest;
import cl.fuelonline.finance.domain.model.Descuento;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DescuentoMapper {

    @Mapping(target = "id",               ignore = true)
    @Mapping(target = "marca",            ignore = true)
    @Mapping(target = "tarjetaProducto",  ignore = true)
    @Mapping(target = "tipoCombustible",  ignore = true)
    @Mapping(target = "activo",           constant = "true")
    @Mapping(target = "createdAt",        ignore = true)
    Descuento toEntity(DescuentoCreateRequest req);

    @Mapping(target = "marcaId",                source = "marca.id")
    @Mapping(target = "marcaNombre",            source = "marca.nombre")
    @Mapping(target = "tarjetaProductoId",      source = "tarjetaProducto.id")
    @Mapping(target = "tarjetaProductoNombre",  source = "tarjetaProducto.nombre")
    @Mapping(target = "tipoCombustibleId",      source = "tipoCombustible.id")
    @Mapping(target = "tipoCombustibleNombre",  source = "tipoCombustible.nombre")
    DescuentoResponse toResponse(Descuento entity);

    @Mapping(target = "id",               ignore = true)
    @Mapping(target = "marca",            ignore = true)
    @Mapping(target = "tarjetaProducto",  ignore = true)
    @Mapping(target = "tipoCombustible",  ignore = true)
    @Mapping(target = "activo",           ignore = true)
    @Mapping(target = "createdAt",        ignore = true)
    void updateEntity(DescuentoUpdateRequest req, @MappingTarget Descuento entity);
}
