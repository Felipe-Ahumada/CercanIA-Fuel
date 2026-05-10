package cl.fuelonline.user.application.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String mensaje) {
        super(mensaje);
    }
}
