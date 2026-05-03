package cl.bencinaenlinea.usuario.application.service;

import cl.bencinaenlinea.shared.exception.ResourceNotFoundException;
import cl.bencinaenlinea.usuario.application.dto.UsuarioCreateRequest;
import cl.bencinaenlinea.usuario.application.dto.UsuarioResponse;
import cl.bencinaenlinea.usuario.application.dto.UsuarioUpdateRequest;
import cl.bencinaenlinea.usuario.application.exception.UsuarioYaExisteException;
import cl.bencinaenlinea.usuario.application.mapper.UsuarioMapper;
import cl.bencinaenlinea.usuario.domain.model.Rol;
import cl.bencinaenlinea.usuario.domain.model.Usuario;
import cl.bencinaenlinea.usuario.domain.repository.RolRepository;
import cl.bencinaenlinea.usuario.domain.repository.UsuarioRepository;
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
