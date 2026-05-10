package cl.fuelonline.station.application.mapper;

import cl.fuelonline.station.application.dto.PrecioActualResponse;
import cl.fuelonline.station.application.dto.PrecioHistorialResponse;
import cl.fuelonline.station.domain.model.PrecioHistorial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PrecioMapper {

    @Mapping(target = "tipoCombustibleId",     source = "tipoCombustible.id")
    @Mapping(target = "tipoCombustibleNombre", source = "tipoCombustible.nombre")
    PrecioActualResponse toActual(PrecioHistorial p);

    PrecioHistorialResponse toHistorial(PrecioHistorial p);
}
