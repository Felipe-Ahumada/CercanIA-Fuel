package cl.fuelonline.finance.application.service;

import cl.fuelonline.finance.application.dto.TarjetaProductoCreateRequest;
import cl.fuelonline.finance.application.dto.TarjetaProductoResponse;
import cl.fuelonline.finance.application.dto.TarjetaProductoUpdateRequest;
import cl.fuelonline.finance.application.exception.TarjetaProductoYaExisteException;
import cl.fuelonline.finance.application.mapper.TarjetaProductoMapper;
import cl.fuelonline.finance.domain.model.Banco;
import cl.fuelonline.finance.domain.model.TarjetaProducto;
import cl.fuelonline.finance.domain.repository.BancoRepository;
import cl.fuelonline.finance.domain.repository.TarjetaProductoRepository;
import cl.fuelonline.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TarjetaProductoService {

    private final TarjetaProductoRepository tarjetaRepository;
    private final BancoRepository bancoRepository;
    private final TarjetaProductoMapper mapper;

    public List<TarjetaProductoResponse> listar() {
        return tarjetaRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    public List<TarjetaProductoResponse> listarPorBanco(Integer bancoId) {
        return tarjetaRepository.findAllByBanco_IdOrderByNombreAsc(bancoId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    public TarjetaProductoResponse buscarPorId(Integer id) {
        return mapper.toResponse(obtener(id));
    }

    @Transactional
    public TarjetaProductoResponse crear(TarjetaProductoCreateRequest req) {
        Banco banco = bancoRepository.findById(req.bancoId())
                .orElseThrow(() -> new ResourceNotFoundException("Banco no encontrado: " + req.bancoId()));

        if (tarjetaRepository.existsByBanco_IdAndNombreIgnoreCase(req.bancoId(), req.nombre())) {
            throw new TarjetaProductoYaExisteException(
                    "Producto '%s' ya existe para banco %d".formatted(req.nombre(), req.bancoId()));
        }

        TarjetaProducto entity = mapper.toEntity(req);
        entity.setBanco(banco);
        return mapper.toResponse(tarjetaRepository.save(entity));
    }

    @Transactional
    public TarjetaProductoResponse actualizar(Integer id, TarjetaProductoUpdateRequest req) {
        TarjetaProducto entity = obtener(id);

        if (req.nombre() != null && !req.nombre().equalsIgnoreCase(entity.getNombre())
                && tarjetaRepository.existsByBanco_IdAndNombreIgnoreCase(
                        entity.getBanco().getId(), req.nombre())) {
            throw new TarjetaProductoYaExisteException(
                    "Producto '%s' ya existe para banco %d".formatted(req.nombre(), entity.getBanco().getId()));
        }

        mapper.updateEntity(req, entity);
        return mapper.toResponse(entity);
    }

    @Transactional
    public void eliminar(Integer id) {
        TarjetaProducto entity = obtener(id);
        entity.setActivo(Boolean.FALSE);
    }

    private TarjetaProducto obtener(Integer id) {
        return tarjetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto de tarjeta no encontrado: " + id));
    }
}
