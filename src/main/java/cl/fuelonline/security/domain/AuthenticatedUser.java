package cl.fuelonline.security.domain;

import cl.fuelonline.user.domain.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Principal autenticado.
 * Conserva el ID local del Usuario y un snapshot de email + rol al momento del login,
 * para no abrir una transaccion JPA por cada acceso al principal.
 */
public record AuthenticatedUser(
        UUID usuarioId,
        String firebaseUid,
        String email,
        String rolNombre
) {

    public static AuthenticatedUser fromUsuario(Usuario u) {
        return new AuthenticatedUser(
                u.getId(),
                u.getFirebaseUid(),
                u.getEmail(),
                u.getRol() != null ? u.getRol().getNombre() : null);
    }

    public Collection<? extends GrantedAuthority> authorities() {
        if (rolNombre == null || rolNombre.isBlank()) return List.of();
        return List.of(new SimpleGrantedAuthority("ROLE_" + rolNombre.toUpperCase()));
    }
}
