package cl.fuelonline.user.application.service;

import cl.fuelonline.shared.exception.ResourceNotFoundException;
import cl.fuelonline.user.application.dto.CompleteProfileRequest;
import cl.fuelonline.user.application.dto.UserCreateRequest;
import cl.fuelonline.user.application.dto.UserResponse;
import cl.fuelonline.user.application.dto.UserUpdateRequest;
import cl.fuelonline.user.application.exception.UserAlreadyExistsException;
import cl.fuelonline.user.application.mapper.UserMapper;
import cl.fuelonline.user.domain.model.AuthProvider;
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

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper mapper;

    public Page<UserResponse> list(Pageable pageable) {
        return userRepository.findAll(pageable).map(mapper::toResponse);
    }

    public UserResponse findById(UUID id) {
        return mapper.toResponse(get(id));
    }

    @Transactional
    public UserResponse create(UserCreateRequest req) {
        if (userRepository.existsByEmailIgnoreCase(req.email())) {
            throw new UserAlreadyExistsException("Email already registered: " + req.email());
        }
        if (userRepository.existsByRut(req.rut())) {
            throw new UserAlreadyExistsException("RUT already registered: " + req.rut());
        }

        Role role = roleRepository.findById(req.roleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + req.roleId()));

        User nuevo = mapper.toEntity(req);
        nuevo.setRole(role);
        nuevo.setAuthProvider(AuthProvider.GOOGLE);

        return mapper.toResponse(userRepository.save(nuevo));
    }

    @Transactional
    public UserResponse update(UUID id, UserUpdateRequest req) {
        User user = get(id);

        if (req.email() != null && !req.email().equalsIgnoreCase(user.getEmail())
                && userRepository.existsByEmailIgnoreCase(req.email())) {
            throw new UserAlreadyExistsException("Email already registered: " + req.email());
        }

        mapper.updateEntity(req, user);

        if (req.roleId() != null) {
            Role role = roleRepository.findById(req.roleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + req.roleId()));
            user.setRole(role);
        }

        return mapper.toResponse(user);
    }

    @Transactional
    public UserResponse completeProfile(CompleteProfileRequest req) {
        User user = userRepository.findByEmailIgnoreCase(req.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + req.email()));
        user.setFirstName(req.firstName());
        user.setLastName(req.lastName());
        user.setSecondLastName(req.secondLastName());
        user.setRut(req.rut());
        user.setBirthDate(req.birthDate());
        return mapper.toResponse(user);
    }

    @Transactional
    public void delete(UUID id) {
        User user = get(id);
        user.deactivate();
    }

    private User get(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }
}
