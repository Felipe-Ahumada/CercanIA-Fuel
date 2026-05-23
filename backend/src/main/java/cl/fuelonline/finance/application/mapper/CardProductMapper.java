package cl.fuelonline.finance.application.mapper;

import cl.fuelonline.finance.application.dto.CardProductCreateRequest;
import cl.fuelonline.finance.application.dto.CardProductResponse;
import cl.fuelonline.finance.application.dto.CardProductUpdateRequest;
import cl.fuelonline.finance.domain.model.CardProduct;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CardProductMapper {

    @Mapping(target = "id",     ignore = true)
    @Mapping(target = "bank",  ignore = true)
    @Mapping(target = "active", constant = "true")
    CardProduct toEntity(CardProductCreateRequest req);

    @Mapping(target = "bankId",     source = "bank.id")
    @Mapping(target = "bankName", source = "bank.name")
    CardProductResponse toResponse(CardProduct entity);

    @Mapping(target = "id",     ignore = true)
    @Mapping(target = "bank",  ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntity(CardProductUpdateRequest req, @MappingTarget CardProduct entity);
}
