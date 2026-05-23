package cl.fuelonline.transaction.application.mapper;

import cl.fuelonline.transaction.application.dto.MonthlyStatResponse;
import cl.fuelonline.transaction.application.dto.TransactionResponse;
import cl.fuelonline.transaction.domain.model.Transaction;
import org.mapstruct.*;

import java.math.BigDecimal;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {

    @Mapping(target = "userId",          source = "user.id")
    @Mapping(target = "vehicleId",       source = "vehicle.id")
    @Mapping(target = "stationId",       source = "station.id")
    @Mapping(target = "stationName",     source = "station.name")
    @Mapping(target = "stationBrand",    source = "station.brand.name")
    @Mapping(target = "fuelTypeId",      source = "fuelType.id")
    @Mapping(target = "fuelTypeName",    source = "fuelType.name")
    @Mapping(target = "cardProductId",   source = "cardProduct.id")
    @Mapping(target = "cardProductName", source = "cardProduct.name")
    @Mapping(target = "discountId",      source = "discount.id")
    TransactionResponse toResponse(Transaction entity);

    /** Maps a native-query Object[] row from sumByMonth() to a monthly stat DTO. */
    default MonthlyStatResponse toMonthlyStat(Object[] row) {
        String[] months = {"", "Ene", "Feb", "Mar", "Abr", "May", "Jun",
                           "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        int month = ((Number) row[1]).intValue();
        BigDecimal saved  = row[2] instanceof BigDecimal b ? b : new BigDecimal(row[2].toString());
        BigDecimal litres = row[3] instanceof BigDecimal b ? b : new BigDecimal(row[3].toString());
        return new MonthlyStatResponse(months[month], saved, litres);
    }
}