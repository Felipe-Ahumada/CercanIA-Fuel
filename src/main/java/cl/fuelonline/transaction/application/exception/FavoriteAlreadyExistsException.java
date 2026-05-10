package cl.fuelonline.transaction.application.exception;

public class FavoriteAlreadyExistsException extends RuntimeException {
    public FavoriteAlreadyExistsException(String mensaje) {
        super(mensaje);
    }
}
