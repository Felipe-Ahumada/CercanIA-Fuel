package cl.fuelonline.finance.application.mapper;

import cl.fuelonline.finance.application.dto.DiscountCreateRequest;
import cl.fuelonline.finance.application.dto.DiscountResponse;
import cl.fuelonline.finance.application.dto.DiscountUpdateRequest;
import cl.fuelonline.finance.domain.model.Discount;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DiscountMapper {

    @Mapping(target = "id",               ignore = true)
    @Mapping(target = "brand",            ignore = true)
    @Mapping(target = "cardProduct",  ignore = true)
    @Mapping(target = "fuelType",  ignore = true)
    @Mapping(target = "active",           constant = "true")
    Discount toEntity(DiscountCreateRequest req);

    @Mapping(target = "brandId",          source = "brand.id")
    @Mapping(target = "brandName",        source = "brand.name")
    @Mapping(target = "cardProductId",    source = "cardProduct.id")
    @Mapping(target = "cardProductName",  source = "cardProduct.name")
    @Mapping(target = "bankName",         source = "cardProduct.bank.name")
    @Mapping(target = "fuelTypeId",       source = "fuelType.id")
    @Mapping(target = "fuelTypeName",     source = "fuelType.name")
    DiscountResponse toResponse(Discount entity);

    @Mapping(target = "id",               ignore = true)
    @Mapping(target = "brand",            ignore = true)
    @Mapping(target = "cardProduct",  ignore = true)
    @Mapping(target = "fuelType",  ignore = true)
    @Mapping(target = "active",           ignore = true)
    @Mapping(target = "createdAt",        ignore = true)
    void updateEntity(DiscountUpdateRequest req, @MappingTarget Discount entity);
}
