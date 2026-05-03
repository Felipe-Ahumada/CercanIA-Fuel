package cl.bencinaenlinea.finanzas.application.service;

import cl.bencinaenlinea.finanzas.application.dto.BancoCreateRequest;
import cl.bencinaenlinea.finanzas.application.dto.BancoResponse;
import cl.bencinaenlinea.finanzas.application.dto.BancoUpdateRequest;
import cl.bencinaenlinea.finanzas.application.exception.BancoYaExisteException;
import cl.bencinaenlinea.finanzas.application.mapper.BancoMapper;
import cl.bencinaenlinea.finanzas.domain.model.Banco;
import cl.bencinaenlinea.finanzas.domain.repository.BancoRepository;
import cl.bencinaenlinea.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BancoService {

    private final BancoRepository bancoRepository;
    private final BancoMapper mapper;

    public Page<BancoResponse> listar(Pageable pageable) {
        return bancoRepository.findAll(pageable).map(mapper::toResponse);
    }

    public BancoResponse buscarPorId(Integer id) {
        return mapper.toResponse(obtener(id));
    }

    @Transactional
    public BancoResponse crear(BancoCreateRequest req) {
        if (bancoRepository.existsByCodigoIgnoreCase(req.codigo())) {
            throw new BancoYaExisteException("Codigo de banco ya registrado: " + req.codigo());
        }
        return mapper.toResponse(bancoRepository.save(mapper.toEntity(req)));
    }

    @Transactional
    public BancoResponse actualizar(Integer id, BancoUpdateRequest req) {
        Banco banco = obtener(id);

        if (req.codigo() != null && !req.codigo().equalsIgnoreCase(banco.getCodigo())
                && bancoRepository.existsByCodigoIgnoreCase(req.codigo())) {
            throw new BancoYaExisteException("Codigo de banco ya registrado: " + req.codigo());
        }

        mapper.updateEntity(req, banco);
        return mapper.toResponse(banco);
    }

    @Transactional
    public void eliminar(Integer id) {
        Banco banco = obtener(id);
        banco.setActivo(Boolean.FALSE);
    }

    private Banco obtener(Integer id) {
        return bancoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banco no encontrado: " + id));
    }
}
