package cl.bencinaenlinea.bencinera.application.mapper;

import cl.bencinaenlinea.bencinera.application.dto.PrecioActualResponse;
import cl.bencinaenlinea.bencinera.application.dto.PrecioHistorialResponse;
import cl.bencinaenlinea.bencinera.domain.model.PrecioHistorial;
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
