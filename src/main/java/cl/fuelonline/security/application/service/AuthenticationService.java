package cl.fuelonline.security.application.service;

import cl.fuelonline.security.application.exception.AuthenticationFailedException;
import cl.fuelonline.security.domain.AuthenticatedUser;
import cl.fuelonline.user.domain.model.User;
import cl.fuelonline.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Resuelve un usuario local a partir de los claims de Firebase (o del email en modo dev).
 * Aplica auto-link: si existe un User con el mismo email pero sin firebaseUid, lo enlaza.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository usuarioRepository;

    @Transactional
    public AuthenticatedUser resolverDesdeFirebase(String firebaseUid, String email) {
        // 1. Por firebaseUid (caso normal: usuario ya enlazado)
        Optional<User> porUid = usuarioRepository.findByFirebaseUid(firebaseUid);
        if (porUid.isPresent()) {
            return AuthenticatedUser.fromUsuario(porUid.get());
        }

        // 2. Por email: si existe, enlazamos el firebaseUid (auto-link)
        if (email != null && !email.isBlank()) {
            Optional<User> porEmail = usuarioRepository.findByEmailIgnoreCase(email);
            if (porEmail.isPresent()) {
                User u = porEmail.get();
                u.setFirebaseUid(firebaseUid);
                return AuthenticatedUser.fromUsuario(u);
            }
        }

        throw new AuthenticationFailedException(
                "No existe un usuario local asociado al token. Registrese en /api/v1/usuarios primero.");
    }

    @Transactional(readOnly = true)
    public AuthenticatedUser resolverDesdeEmail(String email) {
        User u = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new AuthenticationFailedException(
                        "User dev no encontrado: " + email));
        return AuthenticatedUser.fromUsuario(u);
    }
}
