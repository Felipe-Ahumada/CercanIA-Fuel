package cl.fuelonline.security.domain;

import cl.fuelonline.user.domain.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Principal autenticado.
 * Conserva el ID local del User y un snapshot de email + role al momento del login,
 * para no abrir una transaction JPA por cada acceso al principal.
 */
public record AuthenticatedUser(
        UUID userId,
        String firebaseUid,
        String email,
        String roleName
) {

    public static AuthenticatedUser fromUser(User u) {
        return new AuthenticatedUser(
                u.getId(),
                u.getFirebaseUid(),
                u.getEmail(),
                u.getRole() != null ? u.getRole().getName() : null);
    }

    public Collection<? extends GrantedAuthority> authorities() {
        if (roleName == null || roleName.isBlank()) return List.of();
        return List.of(new SimpleGrantedAuthority("ROLE_" + roleName.toUpperCase()));
    }
}
