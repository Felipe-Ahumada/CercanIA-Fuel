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
    private final BankRepository bancoRepository;
    private final CardProductMapper mapper;

    public List<CardProductResponse> listar() {
        return tarjetaRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    public List<CardProductResponse> listarPorBanco(Integer bancoId) {
        return tarjetaRepository.findAllByBanco_IdOrderByNombreAsc(bancoId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    public CardProductResponse buscarPorId(Integer id) {
        return mapper.toResponse(obtener(id));
    }

    @Transactional
    public CardProductResponse crear(CardProductCreateRequest req) {
        Bank banco = bancoRepository.findById(req.bancoId())
                .orElseThrow(() -> new ResourceNotFoundException("Bank no encontrado: " + req.bancoId()));

        if (tarjetaRepository.existsByBanco_IdAndNombreIgnoreCase(req.bancoId(), req.nombre())) {
            throw new CardProductAlreadyExistsException(
                    "Producto '%s' ya existe para banco %d".formatted(req.nombre(), req.bancoId()));
        }

        CardProduct entity = mapper.toEntity(req);
        entity.setBanco(banco);
        return mapper.toResponse(tarjetaRepository.save(entity));
    }

    @Transactional
    public CardProductResponse actualizar(Integer id, CardProductUpdateRequest req) {
        CardProduct entity = obtener(id);

        if (req.nombre() != null && !req.nombre().equalsIgnoreCase(entity.getNombre())
                && tarjetaRepository.existsByBanco_IdAndNombreIgnoreCase(
                        entity.getBanco().getId(), req.nombre())) {
            throw new CardProductAlreadyExistsException(
                    "Producto '%s' ya existe para banco %d".formatted(req.nombre(), entity.getBanco().getId()));
        }

        mapper.updateEntity(req, entity);
        return mapper.toResponse(entity);
    }

    @Transactional
    public void eliminar(Integer id) {
        CardProduct entity = obtener(id);
        entity.setActivo(Boolean.FALSE);
    }

    private CardProduct obtener(Integer id) {
        return tarjetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto de tarjeta no encontrado: " + id));
    }
}
