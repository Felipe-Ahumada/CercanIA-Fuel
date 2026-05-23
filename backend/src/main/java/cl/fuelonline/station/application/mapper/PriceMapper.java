package cl.fuelonline.station.application.mapper;

import cl.fuelonline.station.application.dto.CurrentPriceResponse;
import cl.fuelonline.station.application.dto.PriceHistoryResponse;
import cl.fuelonline.station.domain.model.PriceHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PriceMapper {

    @Mapping(target = "fuelTypeId",   source = "fuelType.id")
    @Mapping(target = "fuelTypeName", source = "fuelType.name")
    CurrentPriceResponse toCurrent(PriceHistory p);

    PriceHistoryResponse toHistory(PriceHistory p);
}
