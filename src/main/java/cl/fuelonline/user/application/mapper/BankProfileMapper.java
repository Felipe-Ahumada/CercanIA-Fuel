package cl.fuelonline.user.application.mapper;

import cl.fuelonline.user.application.dto.BankConvenioItem;
import cl.fuelonline.user.domain.model.UserBankConvenio;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BankProfileMapper {

    BankConvenioItem toItem(UserBankConvenio convenio);
}