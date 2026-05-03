package cl.bencinaenlinea.transaccion.application.mapper;

import cl.bencinaenlinea.transaccion.application.dto.TransaccionResponse;
import cl.bencinaenlinea.transaccion.domain.model.Transaccion;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransaccionMapper {

    @Mapping(target = "usuarioId",              source = "usuario.id")
    @Mapping(target = "vehiculoId",             source = "vehiculo.id")
    @Mapping(target = "bencineraId",            source = "bencinera.id")
    @Mapping(target = "bencineraNombre",        source = "bencinera.nombre")
    @Mapping(target = "tipoCombustibleId",      source = "tipoCombustible.id")
    @Mapping(target = "tipoCombustibleNombre",  source = "tipoCombustible.nombre")
    @Mapping(target = "tarjetaProductoId",      source = "tarjetaProducto.id")
    @Mapping(target = "tarjetaProductoNombre",  source = "tarjetaProducto.nombre")
    @Mapping(target = "descuentoId",            source = "descuento.id")
    TransaccionResponse toResponse(Transaccion entity);
}
