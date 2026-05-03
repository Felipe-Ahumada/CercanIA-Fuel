package cl.bencinaenlinea.finanzas.application.mapper;

import cl.bencinaenlinea.finanzas.application.dto.BancoCreateRequest;
import cl.bencinaenlinea.finanzas.application.dto.BancoResponse;
import cl.bencinaenlinea.finanzas.application.dto.BancoUpdateRequest;
import cl.bencinaenlinea.finanzas.domain.model.Banco;
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
