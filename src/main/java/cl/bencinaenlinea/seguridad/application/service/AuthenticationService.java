package cl.bencinaenlinea.seguridad.application.service;

import cl.bencinaenlinea.seguridad.application.exception.AuthenticationFailedException;
import cl.bencinaenlinea.seguridad.domain.AuthenticatedUser;
import cl.bencinaenlinea.usuario.domain.model.Usuario;
import cl.bencinaenlinea.usuario.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Resuelve un usuario local a partir de los claims de Firebase (o del email en modo dev).
 * Aplica auto-link: si existe un Usuario con el mismo email pero sin firebaseUid, lo enlaza.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public AuthenticatedUser resolverDesdeFirebase(String firebaseUid, String email) {
        // 1. Por firebaseUid (caso normal: usuario ya enlazado)
        Optional<Usuario> porUid = usuarioRepository.findByFirebaseUid(firebaseUid);
        if (porUid.isPresent()) {
            return AuthenticatedUser.fromUsuario(porUid.get());
        }

        // 2. Por email: si existe, enlazamos el firebaseUid (auto-link)
        if (email != null && !email.isBlank()) {
            Optional<Usuario> porEmail = usuarioRepository.findByEmailIgnoreCase(email);
            if (porEmail.isPresent()) {
                Usuario u = porEmail.get();
                u.setFirebaseUid(firebaseUid);
                return AuthenticatedUser.fromUsuario(u);
            }
        }

        throw new AuthenticationFailedException(
                "No existe un usuario local asociado al token. Registrese en /api/v1/usuarios primero.");
    }

    @Transactional(readOnly = true)
    public AuthenticatedUser resolverDesdeEmail(String email) {
        Usuario u = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new AuthenticationFailedException(
                        "Usuario dev no encontrado: " + email));
        return AuthenticatedUser.fromUsuario(u);
    }
}
