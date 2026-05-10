package cl.fuelonline.transaction.application.exception;

public class RatingAlreadyExistsException extends RuntimeException {
    public RatingAlreadyExistsException(String mensaje) {
        super(mensaje);
    }
}
