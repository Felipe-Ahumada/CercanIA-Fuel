package cl.fuelonline.user.application.service;

import cl.fuelonline.user.application.dto.BankConvenioItem;
import cl.fuelonline.user.application.dto.BankProfileRequest;
import cl.fuelonline.user.application.dto.BankProfileResponse;
import cl.fuelonline.user.application.mapper.BankProfileMapper;
import cl.fuelonline.user.domain.model.UserBankConvenio;
import cl.fuelonline.user.domain.repository.UserBankConvenioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserBankProfileService {

    private final UserBankConvenioRepository repository;
    private final BankProfileMapper bankProfileMapper;

    @Transactional(readOnly = true)
    public BankProfileResponse getProfile(UUID userId) {
        List<BankConvenioItem> items = repository.findByUserId(userId).stream()
                .map(bankProfileMapper::toItem)
                .toList();
        return new BankProfileResponse(userId, items);
    }

    @Transactional
    public BankProfileResponse saveProfile(UUID userId, BankProfileRequest req) {
        repository.deleteByUserId(userId);

        List<UserBankConvenio> entities = req.convenios().stream()
                .map(item -> UserBankConvenio.builder()
                        .userId(userId)
                        .bank(item.bank())
                        .cardType(item.cardType())
                        .cardProductId(item.cardProductId())
                        .build())
                .toList();
        repository.saveAll(entities);

        return new BankProfileResponse(userId, req.convenios());
    }
}