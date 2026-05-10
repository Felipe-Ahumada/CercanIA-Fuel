package cl.bencinaenlinea.seguridad.application.exception;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationFailedException extends AuthenticationException {
    public AuthenticationFailedException(String mensaje) {
        super(mensaje);
    }

    public AuthenticationFailedException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
