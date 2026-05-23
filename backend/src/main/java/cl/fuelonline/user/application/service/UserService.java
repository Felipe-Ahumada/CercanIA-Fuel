package cl.fuelonline.user.application.service;

import cl.fuelonline.shared.exception.ResourceNotFoundException;
import cl.fuelonline.shared.util.RutUtils;
import cl.fuelonline.user.application.dto.AdminUserResponse;
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
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper mapper;
    private final EntityManager em;

    private static final String ADMIN_LIST_SQL =
            "SELECT u.id, u.email, u.rut, u.first_name, u.middle_name, u.last_name, " +
            "       u.second_last_name, u.birth_date, u.role_id, r.name AS role_name, " +
            "       u.active, u.created_at, u.updated_at, " +
            "       COUNT(DISTINCT v.id)           AS vehicle_count, " +
            "       COUNT(DISTINCT t.id)           AS total_transactions, " +
            "       COALESCE(SUM(t.discount_amount), 0) AS total_savings " +
            "FROM `user` u " +
            "JOIN role r ON u.role_id = r.id " +
            "LEFT JOIN vehicle v    ON v.user_id = u.id AND v.active = 1 " +
            "LEFT JOIN `transaction` t ON t.user_id = u.id AND t.deleted_at IS NULL " +
            "GROUP BY u.id, u.email, u.rut, u.first_name, u.middle_name, u.last_name, " +
            "         u.second_last_name, u.birth_date, u.role_id, r.name, " +
            "         u.active, u.created_at, u.updated_at " +
            "ORDER BY u.active DESC, u.created_at DESC";

    public Page<AdminUserResponse> listAdmin(Pageable pageable) {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(ADMIN_LIST_SQL)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long total = ((Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM `user`").getSingleResult()).longValue();

        List<AdminUserResponse> content = rows.stream().map(r -> new AdminUserResponse(
                (String)  r[0],                                    // id
                (String)  r[1],                                    // email
                (String)  r[2],                                    // rut
                (String)  r[3],                                    // firstName
                (String)  r[4],                                    // middleName
                (String)  r[5],                                    // lastName
                (String)  r[6],                                    // secondLastName
                r[7]  != null ? r[7].toString()  : null,           // birthDate
                ((Number) r[8]).intValue(),                        // roleId
                (String)  r[9],                                    // roleName
                toBoolean(r[10]),                                  // active
                r[11] != null ? r[11].toString() : null,           // createdAt
                r[12] != null ? r[12].toString() : null,           // updatedAt
                ((Number) r[13]).longValue(),                      // vehicleCount
                ((Number) r[14]).longValue(),                      // totalTransactions
                ((Number) r[15]).doubleValue()                     // totalSavings
        )).toList();

        return new PageImpl<>(content, pageable, total);
    }

    public UserResponse findById(UUID id) {
        return mapper.toResponse(get(id));
    }

    @Transactional
    public UserResponse create(UserCreateRequest req) {
        if (userRepository.existsByEmailIgnoreCase(req.email())) {
            throw new UserAlreadyExistsException("Email already registered: " + req.email());
        }
        final String normalizedRut = RutUtils.normalize(req.rut());
        if (userRepository.existsByRut(normalizedRut)) {
            throw new UserAlreadyExistsException("RUT already registered: " + normalizedRut);
        }

        Role role = roleRepository.findById(req.roleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + req.roleId()));

        User nuevo = mapper.toEntity(req);
        nuevo.setRole(role);
        nuevo.setRut(normalizedRut);
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
        user.setRut(RutUtils.normalize(req.rut()));
        user.setBirthDate(req.birthDate());
        return mapper.toResponse(user);
    }

    @Transactional
    public void setActive(UUID id, boolean active) {
        int rows = userRepository.setActive(id.toString(), active);
        if (rows == 0) {
            throw new ResourceNotFoundException("User not found: " + id);
        }
    }

    private static boolean toBoolean(Object val) {
        if (val instanceof Boolean b) return b;
        if (val instanceof Number n)  return n.intValue() != 0;
        if (val instanceof byte[] b)  return b.length > 0 && b[0] != 0;
        return false;
    }

    private User get(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }
}
