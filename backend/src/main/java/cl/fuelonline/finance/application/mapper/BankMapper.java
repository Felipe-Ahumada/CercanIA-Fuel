package cl.fuelonline.finance.application.mapper;

import cl.fuelonline.finance.application.dto.BankCreateRequest;
import cl.fuelonline.finance.application.dto.BankResponse;
import cl.fuelonline.finance.application.dto.BankUpdateRequest;
import cl.fuelonline.finance.domain.model.Bank;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BankMapper {

    @Mapping(target = "id",     ignore = true)
    @Mapping(target = "active", constant = "true")
    Bank toEntity(BankCreateRequest req);

    BankResponse toResponse(Bank bank);

    @Mapping(target = "id",     ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntity(BankUpdateRequest req, @MappingTarget Bank entity);
}
