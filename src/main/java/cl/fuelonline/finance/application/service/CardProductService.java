package cl.fuelonline.finance.application.service;

import cl.fuelonline.finance.application.dto.CardProductCreateRequest;
import cl.fuelonline.finance.application.dto.CardProductResponse;
import cl.fuelonline.finance.application.dto.CardProductUpdateRequest;
import cl.fuelonline.finance.application.exception.CardProductAlreadyExistsException;
import cl.fuelonline.finance.application.mapper.CardProductMapper;
import cl.fuelonline.finance.domain.model.Bank;
import cl.fuelonline.finance.domain.model.CardProduct;
import cl.fuelonline.finance.domain.repository.BankRepository;
import cl.fuelonline.finance.domain.repository.CardProductRepository;
import cl.fuelonline.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardProductService {

    private final CardProductRepository tarjetaRepository;
    private final BankRepository bankRepository;
    private final CardProductMapper mapper;

    public List<CardProductResponse> list() {
        return tarjetaRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    public List<CardProductResponse> listByBank(Integer bankId) {
        return tarjetaRepository.findAllByBanco_IdOrderByNombreAsc(bankId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    public CardProductResponse findById(Integer id) {
        return mapper.toResponse(get(id));
    }

    @Transactional
    public CardProductResponse create(CardProductCreateRequest req) {
        Bank bank = bankRepository.findById(req.bankId())
                .orElseThrow(() -> new ResourceNotFoundException("Bank no encontrado: " + req.bankId()));

        if (tarjetaRepository.existsByBanco_IdAndNombreIgnoreCase(req.bankId(), req.name())) {
            throw new CardProductAlreadyExistsException(
                    "Producto '%s' ya existe para bank %d".formatted(req.name(), req.bankId()));
        }

        CardProduct entity = mapper.toEntity(req);
        entity.setBank(bank);
        return mapper.toResponse(tarjetaRepository.save(entity));
    }

    @Transactional
    public CardProductResponse update(Integer id, CardProductUpdateRequest req) {
        CardProduct entity = get(id);

        if (req.name() != null && !req.name().equalsIgnoreCase(entity.getName())
                && tarjetaRepository.existsByBanco_IdAndNombreIgnoreCase(
                        entity.getBank().getId(), req.name())) {
            throw new CardProductAlreadyExistsException(
                    "Producto '%s' ya existe para bank %d".formatted(req.name(), entity.getBank().getId()));
        }

        mapper.updateEntity(req, entity);
        return mapper.toResponse(entity);
    }

    @Transactional
    public void delete(Integer id) {
        CardProduct entity = get(id);
        entity.setActive(Boolean.FALSE);
    }

    private CardProduct get(Integer id) {
        return tarjetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto de tarjeta no encontrado: " + id));
    }
}
