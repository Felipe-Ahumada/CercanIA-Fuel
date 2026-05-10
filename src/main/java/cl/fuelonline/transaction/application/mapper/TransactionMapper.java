package cl.fuelonline.transaction.application.mapper;

import cl.fuelonline.transaction.application.dto.TransactionResponse;
import cl.fuelonline.transaction.domain.model.Transaction;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {

    @Mapping(target = "usuarioId",              source = "usuario.id")
    @Mapping(target = "vehiculoId",             source = "vehiculo.id")
    @Mapping(target = "bencineraId",            source = "bencinera.id")
    @Mapping(target = "bencineraNombre",        source = "bencinera.nombre")
    @Mapping(target = "tipoCombustibleId",      source = "tipoCombustible.id")
    @Mapping(target = "tipoCombustibleNombre",  source = "tipoCombustible.nombre")
    @Mapping(target = "tarjetaProductoId",      source = "tarjetaProducto.id")
    @Mapping(target = "tarjetaProductoNombre",  source = "tarjetaProducto.nombre")
    @Mapping(target = "descuentoId",            source = "descuento.id")
    TransactionResponse toResponse(Transaction entity);
}
