package cl.fuelonline.user.application.service;

import cl.fuelonline.shared.exception.ResourceNotFoundException;
import cl.fuelonline.user.application.dto.UserCreateRequest;
import cl.fuelonline.user.application.dto.UserResponse;
import cl.fuelonline.user.application.dto.UserUpdateRequest;
import cl.fuelonline.user.application.exception.UserAlreadyExistsException;
import cl.fuelonline.user.application.mapper.UserMapper;
import cl.fuelonline.user.domain.model.Role;
import cl.fuelonline.user.domain.model.User;
import cl.fuelonline.user.domain.repository.RoleRepository;
import cl.fuelonline.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository usuarioRepository;
    private final RoleRepository rolRepository;
    private final UserMapper mapper;

    public Page<UserResponse> listar(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(mapper::toResponse);
    }

    public UserResponse buscarPorId(UUID id) {
        return mapper.toResponse(obtener(id));
    }

    @Transactional
    public UserResponse crear(UserCreateRequest req) {
        if (usuarioRepository.existsByEmailIgnoreCase(req.email())) {
            throw new UserAlreadyExistsException("Email ya registrado: " + req.email());
        }
        if (usuarioRepository.existsByRut(req.rut())) {
            throw new UserAlreadyExistsException("RUT ya registrado: " + req.rut());
        }

        Role rol = rolRepository.findById(req.rolId())
                .orElseThrow(() -> new ResourceNotFoundException("Role no encontrado: " + req.rolId()));

        User nuevo = mapper.toEntity(req);
        nuevo.setRol(rol);

        return mapper.toResponse(usuarioRepository.save(nuevo));
    }

    @Transactional
    public UserResponse actualizar(UUID id, UserUpdateRequest req) {
        User usuario = obtener(id);

        if (req.email() != null && !req.email().equalsIgnoreCase(usuario.getEmail())
                && usuarioRepository.existsByEmailIgnoreCase(req.email())) {
            throw new UserAlreadyExistsException("Email ya registrado: " + req.email());
        }

        mapper.updateEntity(req, usuario);

        if (req.rolId() != null) {
            Role rol = rolRepository.findById(req.rolId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role no encontrado: " + req.rolId()));
            usuario.setRol(rol);
        }

        return mapper.toResponse(usuario);
    }

    @Transactional
    public void eliminar(UUID id) {
        User usuario = obtener(id);
        usuario.desactivar();
    }

    private User obtener(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User no encontrado: " + id));
    }
}
