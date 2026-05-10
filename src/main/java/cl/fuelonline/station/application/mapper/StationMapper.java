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
    @Mapping(target = "brand",             ignore = true)
    @Mapping(target = "commune",            ignore = true)
    @Mapping(target = "syncAt",            ignore = true)
    @Mapping(target = "inMaintenance",   constant = "false")
    @Mapping(target = "active",            constant = "true")
    Station toEntity(StationCreateRequest req);

    @Mapping(target = "id",           source = "b.id")
    @Mapping(target = "apiCode",    source = "b.apiCode")
    @Mapping(target = "brandId",      source = "b.brand.id")
    @Mapping(target = "brandName",  source = "b.brand.name")
    @Mapping(target = "communeId",     source = "b.commune.id")
    @Mapping(target = "communeName", source = "b.commune.name")
    @Mapping(target = "regionId",     source = "b.commune.region.id")
    @Mapping(target = "prices",      source = "prices")
    StationResponse toResponse(Station b, List<CurrentPriceResponse> prices);

    @Mapping(target = "id",          source = "b.id")
    @Mapping(target = "name",      source = "b.name")
    @Mapping(target = "brand",       source = "b.brand.name")
    @Mapping(target = "address",   source = "b.address")
    @Mapping(target = "latitude",     source = "b.latitude")
    @Mapping(target = "longitude",    source = "b.longitude")
    @Mapping(target = "inMaintenance", source = "b.inMaintenance")
    @Mapping(target = "distanciaKm", source = "distanciaKm")
    StationSummaryResponse toSummary(Station b, Double distanciaKm);

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "brand",     ignore = true)
    @Mapping(target = "commune",    ignore = true)
    @Mapping(target = "apiCode", ignore = true)
    @Mapping(target = "active",    ignore = true)
    @Mapping(target = "syncAt",    ignore = true)
    void updateEntity(StationUpdateRequest req, @MappingTarget Station entity);
}
