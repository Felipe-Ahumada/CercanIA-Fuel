package cl.fuelonline.finance.application.mapper;

import cl.fuelonline.finance.application.dto.DiscountCreateRequest;
import cl.fuelonline.finance.application.dto.DiscountResponse;
import cl.fuelonline.finance.application.dto.DiscountUpdateRequest;
import cl.fuelonline.finance.domain.model.Discount;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DiscountMapper {

    @Mapping(target = "id",               ignore = true)
    @Mapping(target = "marca",            ignore = true)
    @Mapping(target = "tarjetaProducto",  ignore = true)
    @Mapping(target = "tipoCombustible",  ignore = true)
    @Mapping(target = "activo",           constant = "true")
    @Mapping(target = "createdAt",        ignore = true)
    Discount toEntity(DiscountCreateRequest req);

    @Mapping(target = "marcaId",                source = "marca.id")
    @Mapping(target = "marcaNombre",            source = "marca.nombre")
    @Mapping(target = "tarjetaProductoId",      source = "tarjetaProducto.id")
    @Mapping(target = "tarjetaProductoNombre",  source = "tarjetaProducto.nombre")
    @Mapping(target = "tipoCombustibleId",      source = "tipoCombustible.id")
    @Mapping(target = "tipoCombustibleNombre",  source = "tipoCombustible.nombre")
    DiscountResponse toResponse(Discount entity);

    @Mapping(target = "id",               ignore = true)
    @Mapping(target = "marca",            ignore = true)
    @Mapping(target = "tarjetaProducto",  ignore = true)
    @Mapping(target = "tipoCombustible",  ignore = true)
    @Mapping(target = "activo",           ignore = true)
    @Mapping(target = "createdAt",        ignore = true)
    void updateEntity(DiscountUpdateRequest req, @MappingTarget Discount entity);
}
