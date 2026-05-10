package cl.fuelonline.station.application.mapper;

import cl.fuelonline.station.application.dto.StationCreateRequest;
import cl.fuelonline.station.application.dto.StationResponse;
import cl.fuelonline.station.application.dto.StationSummaryResponse;
import cl.fuelonline.station.application.dto.StationUpdateRequest;
import cl.fuelonline.station.application.dto.CurrentPriceResponse;
import cl.fuelonline.station.domain.model.Station;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StationMapper {

    @Mapping(target = "id",                ignore = true)
    @Mapping(target = "marca",             ignore = true)
    @Mapping(target = "comuna",            ignore = true)
    @Mapping(target = "syncAt",            ignore = true)
    @Mapping(target = "enMantenimiento",   constant = "false")
    @Mapping(target = "activo",            constant = "true")
    Station toEntity(StationCreateRequest req);

    @Mapping(target = "id",           source = "b.id")
    @Mapping(target = "codigoApi",    source = "b.codigoApi")
    @Mapping(target = "marcaId",      source = "b.marca.id")
    @Mapping(target = "marcaNombre",  source = "b.marca.nombre")
    @Mapping(target = "comunaId",     source = "b.comuna.id")
    @Mapping(target = "comunaNombre", source = "b.comuna.nombre")
    @Mapping(target = "regionId",     source = "b.comuna.region.id")
    @Mapping(target = "precios",      source = "precios")
    StationResponse toResponse(Station b, List<CurrentPriceResponse> precios);

    @Mapping(target = "id",          source = "b.id")
    @Mapping(target = "nombre",      source = "b.nombre")
    @Mapping(target = "marca",       source = "b.marca.nombre")
    @Mapping(target = "direccion",   source = "b.direccion")
    @Mapping(target = "latitud",     source = "b.latitud")
    @Mapping(target = "longitud",    source = "b.longitud")
    @Mapping(target = "enMantenimiento", source = "b.enMantenimiento")
    @Mapping(target = "distanciaKm", source = "distanciaKm")
    StationSummaryResponse toSummary(Station b, Double distanciaKm);

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "marca",     ignore = true)
    @Mapping(target = "comuna",    ignore = true)
    @Mapping(target = "codigoApi", ignore = true)
    @Mapping(target = "activo",    ignore = true)
    @Mapping(target = "syncAt",    ignore = true)
    void updateEntity(StationUpdateRequest req, @MappingTarget Station entity);
}
