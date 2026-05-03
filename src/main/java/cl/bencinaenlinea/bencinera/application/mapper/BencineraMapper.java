package cl.bencinaenlinea.bencinera.application.mapper;

import cl.bencinaenlinea.bencinera.application.dto.BencineraCreateRequest;
import cl.bencinaenlinea.bencinera.application.dto.BencineraResponse;
import cl.bencinaenlinea.bencinera.application.dto.BencineraSummaryResponse;
import cl.bencinaenlinea.bencinera.application.dto.BencineraUpdateRequest;
import cl.bencinaenlinea.bencinera.application.dto.PrecioActualResponse;
import cl.bencinaenlinea.bencinera.domain.model.Bencinera;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BencineraMapper {

    @Mapping(target = "id",                ignore = true)
    @Mapping(target = "marca",             ignore = true)
    @Mapping(target = "comuna",            ignore = true)
    @Mapping(target = "syncAt",            ignore = true)
    @Mapping(target = "enMantenimiento",   constant = "false")
    @Mapping(target = "activo",            constant = "true")
    Bencinera toEntity(BencineraCreateRequest req);

    @Mapping(target = "id",           source = "b.id")
    @Mapping(target = "codigoApi",    source = "b.codigoApi")
    @Mapping(target = "marcaId",      source = "b.marca.id")
    @Mapping(target = "marcaNombre",  source = "b.marca.nombre")
    @Mapping(target = "comunaId",     source = "b.comuna.id")
    @Mapping(target = "comunaNombre", source = "b.comuna.nombre")
    @Mapping(target = "regionId",     source = "b.comuna.region.id")
    @Mapping(target = "precios",      source = "precios")
    BencineraResponse toResponse(Bencinera b, List<PrecioActualResponse> precios);

    @Mapping(target = "id",          source = "b.id")
    @Mapping(target = "nombre",      source = "b.nombre")
    @Mapping(target = "marca",       source = "b.marca.nombre")
    @Mapping(target = "direccion",   source = "b.direccion")
    @Mapping(target = "latitud",     source = "b.latitud")
    @Mapping(target = "longitud",    source = "b.longitud")
    @Mapping(target = "enMantenimiento", source = "b.enMantenimiento")
    @Mapping(target = "distanciaKm", source = "distanciaKm")
    BencineraSummaryResponse toSummary(Bencinera b, Double distanciaKm);

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "marca",     ignore = true)
    @Mapping(target = "comuna",    ignore = true)
    @Mapping(target = "codigoApi", ignore = true)
    @Mapping(target = "activo",    ignore = true)
    @Mapping(target = "syncAt",    ignore = true)
    void updateEntity(BencineraUpdateRequest req, @MappingTarget Bencinera entity);
}
