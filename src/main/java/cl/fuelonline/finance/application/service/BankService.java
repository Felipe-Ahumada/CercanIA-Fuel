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

    private final BankRepository bancoRepository;
    private final BankMapper mapper;

    public Page<BankResponse> listar(Pageable pageable) {
        return bancoRepository.findAll(pageable).map(mapper::toResponse);
    }

    public BankResponse buscarPorId(Integer id) {
        return mapper.toResponse(obtener(id));
    }

    @Transactional
    public BankResponse crear(BankCreateRequest req) {
        if (bancoRepository.existsByCodigoIgnoreCase(req.codigo())) {
            throw new BankAlreadyExistsException("Codigo de banco ya registrado: " + req.codigo());
        }
        return mapper.toResponse(bancoRepository.save(mapper.toEntity(req)));
    }

    @Transactional
    public BankResponse actualizar(Integer id, BankUpdateRequest req) {
        Bank banco = obtener(id);

        if (req.codigo() != null && !req.codigo().equalsIgnoreCase(banco.getCodigo())
                && bancoRepository.existsByCodigoIgnoreCase(req.codigo())) {
            throw new BankAlreadyExistsException("Codigo de banco ya registrado: " + req.codigo());
        }

        mapper.updateEntity(req, banco);
        return mapper.toResponse(banco);
    }

    @Transactional
    public void eliminar(Integer id) {
        Bank banco = obtener(id);
        banco.setActivo(Boolean.FALSE);
    }

    private Bank obtener(Integer id) {
        return bancoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank no encontrado: " + id));
    }
}
