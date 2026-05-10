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
    @Mapping(target = "banco",  ignore = true)
    @Mapping(target = "activo", constant = "true")
    CardProduct toEntity(CardProductCreateRequest req);

    @Mapping(target = "bancoId",     source = "banco.id")
    @Mapping(target = "bancoNombre", source = "banco.nombre")
    CardProductResponse toResponse(CardProduct entity);

    @Mapping(target = "id",     ignore = true)
    @Mapping(target = "banco",  ignore = true)
    @Mapping(target = "activo", ignore = true)
    void updateEntity(CardProductUpdateRequest req, @MappingTarget CardProduct entity);
}
