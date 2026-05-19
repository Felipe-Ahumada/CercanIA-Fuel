package cl.fuelonline.security.application.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;

/**
 * Verifies an ID Token issued by Firebase Authentication.
 * Instantiated only by FirebaseConfig when credentials are configured.
 */
@RequiredArgsConstructor
public class FirebaseTokenService {

    private final FirebaseAuth firebaseAuth;

    /**
     * @param idToken token recibido en el header Authorization (sin el prefijo "Bearer ").
     * @return el token decodificado con claims (uid, email, name, etc.)
     * @throws FirebaseAuthException si el token es invalido, expirado o revocado.
     */
    public FirebaseToken verify(String idToken) throws FirebaseAuthException {
        return firebaseAuth.verifyIdToken(idToken, /* checkRevoked */ true);
    }
}
