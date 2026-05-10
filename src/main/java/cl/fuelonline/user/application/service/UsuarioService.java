package cl.fuelonline.user.application.service;

import cl.fuelonline.shared.exception.ResourceNotFoundException;
import cl.fuelonline.user.application.dto.UsuarioCreateRequest;
import cl.fuelonline.user.application.dto.UsuarioResponse;
import cl.fuelonline.user.application.dto.UsuarioUpdateRequest;
import cl.fuelonline.user.application.exception.UsuarioYaExisteException;
import cl.fuelonline.user.application.mapper.UsuarioMapper;
import cl.fuelonline.user.domain.model.Rol;
import cl.fuelonline.user.domain.model.Usuario;
import cl.fuelonline.user.domain.repository.RolRepository;
import cl.fuelonline.user.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioMapper mapper;

    public Page<UsuarioResponse> listar(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(mapper::toResponse);
    }

    public UsuarioResponse buscarPorId(UUID id) {
        return mapper.toResponse(obtener(id));
    }

    @Transactional
    public UsuarioResponse crear(UsuarioCreateRequest req) {
        if (usuarioRepository.existsByEmailIgnoreCase(req.email())) {
            throw new UsuarioYaExisteException("Email ya registrado: " + req.email());
        }
        if (usuarioRepository.existsByRut(req.rut())) {
            throw new UsuarioYaExisteException("RUT ya registrado: " + req.rut());
        }

        Rol rol = rolRepository.findById(req.rolId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + req.rolId()));

        Usuario nuevo = mapper.toEntity(req);
        nuevo.setRol(rol);

        return mapper.toResponse(usuarioRepository.save(nuevo));
    }

    @Transactional
    public UsuarioResponse actualizar(UUID id, UsuarioUpdateRequest req) {
        Usuario usuario = obtener(id);

        if (req.email() != null && !req.email().equalsIgnoreCase(usuario.getEmail())
                && usuarioRepository.existsByEmailIgnoreCase(req.email())) {
            throw new UsuarioYaExisteException("Email ya registrado: " + req.email());
        }

        mapper.updateEntity(req, usuario);

        if (req.rolId() != null) {
            Rol rol = rolRepository.findById(req.rolId())
                    .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + req.rolId()));
            usuario.setRol(rol);
        }

        return mapper.toResponse(usuario);
    }

    @Transactional
    public void eliminar(UUID id) {
        Usuario usuario = obtener(id);
        usuario.desactivar();
    }

    private Usuario obtener(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
    }
}
