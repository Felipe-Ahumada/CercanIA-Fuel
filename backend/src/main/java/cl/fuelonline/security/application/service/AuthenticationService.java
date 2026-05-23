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
 * Resuelve un user local a partir de los claims de Firebase (o del email en modo dev).
 * Aplica auto-link: si existe un User con el mismo email pero sin firebaseUid, lo enlaza.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    @Transactional
    public AuthenticatedUser resolveFromFirebase(String firebaseUid, String email) {
        // 1. Por firebaseUid (caso normal: user ya enlazado)
        Optional<User> porUid = userRepository.findByFirebaseUid(firebaseUid);
        if (porUid.isPresent()) {
            return AuthenticatedUser.fromUser(porUid.get());
        }

        // 2. By email: if it exists, link the firebaseUid (auto-link)
        if (email != null && !email.isBlank()) {
            Optional<User> porEmail = userRepository.findByEmailIgnoreCase(email);
            if (porEmail.isPresent()) {
                User u = porEmail.get();
                u.setFirebaseUid(firebaseUid);
                return AuthenticatedUser.fromUser(u);
            }
        }

        throw new AuthenticationFailedException(
                "No local user is linked to this token. Register at /api/v1/usuarios first.");
    }

    @Transactional(readOnly = true)
    public AuthenticatedUser resolveFromEmail(String email) {
        User u = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new AuthenticationFailedException(
                        "Dev user not found: " + email));
        return AuthenticatedUser.fromUser(u);
    }
}
