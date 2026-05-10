package cl.fuelonline.finance.application.service;

import cl.fuelonline.finance.application.dto.BankCreateRequest;
import cl.fuelonline.finance.application.dto.BankResponse;
import cl.fuelonline.finance.application.dto.BankUpdateRequest;
import cl.fuelonline.finance.application.exception.BankAlreadyExistsException;
import cl.fuelonline.finance.application.mapper.BankMapper;
import cl.fuelonline.finance.domain.model.Bank;
import cl.fuelonline.finance.domain.repository.BankRepository;
import cl.fuelonline.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BankService {

    private final BankRepository bankRepository;
    private final BankMapper mapper;

    public Page<BankResponse> list(Pageable pageable) {
        return bankRepository.findAll(pageable).map(mapper::toResponse);
    }

    public BankResponse findById(Integer id) {
        return mapper.toResponse(get(id));
    }

    @Transactional
    public BankResponse create(BankCreateRequest req) {
        if (bankRepository.existsByCodeIgnoreCase(req.code())) {
            throw new BankAlreadyExistsException("Codigo de bank ya registrado: " + req.code());
        }
        return mapper.toResponse(bankRepository.save(mapper.toEntity(req)));
    }

    @Transactional
    public BankResponse update(Integer id, BankUpdateRequest req) {
        Bank bank = get(id);

        if (req.code() != null && !req.code().equalsIgnoreCase(bank.getCode())
                && bankRepository.existsByCodeIgnoreCase(req.code())) {
            throw new BankAlreadyExistsException("Codigo de bank ya registrado: " + req.code());
        }

        mapper.updateEntity(req, bank);
        return mapper.toResponse(bank);
    }

    @Transactional
    public void delete(Integer id) {
        Bank bank = get(id);
        bank.setActive(Boolean.FALSE);
    }

    private Bank get(Integer id) {
        return bankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank no encontrado: " + id));
    }
}
