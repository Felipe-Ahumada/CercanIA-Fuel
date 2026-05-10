package cl.fuelonline.transaction.application.mapper;

import cl.fuelonline.transaction.application.dto.TransactionResponse;
import cl.fuelonline.transaction.domain.model.Transaction;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {

    @Mapping(target = "userId",              source = "user.id")
    @Mapping(target = "vehicleId",             source = "vehicle.id")
    @Mapping(target = "stationId",            source = "station.id")
    @Mapping(target = "stationName",        source = "station.name")
    @Mapping(target = "fuelTypeId",      source = "fuelType.id")
    @Mapping(target = "fuelTypeName",  source = "fuelType.name")
    @Mapping(target = "cardProductId",      source = "cardProduct.id")
    @Mapping(target = "cardProductName",  source = "cardProduct.name")
    @Mapping(target = "discountId",            source = "discount.id")
    TransactionResponse toResponse(Transaction entity);
}
