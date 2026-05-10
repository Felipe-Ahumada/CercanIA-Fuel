package cl.fuelonline.finance.application.mapper;

import cl.fuelonline.finance.application.dto.BancoCreateRequest;
import cl.fuelonline.finance.application.dto.BancoResponse;
import cl.fuelonline.finance.application.dto.BancoUpdateRequest;
import cl.fuelonline.finance.domain.model.Banco;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BancoMapper {

    @Mapping(target = "id",     ignore = true)
    @Mapping(target = "activo", constant = "true")
    Banco toEntity(BancoCreateRequest req);

    BancoResponse toResponse(Banco banco);

    @Mapping(target = "id",     ignore = true)
    @Mapping(target = "activo", ignore = true)
    void updateEntity(BancoUpdateRequest req, @MappingTarget Banco entity);
}
