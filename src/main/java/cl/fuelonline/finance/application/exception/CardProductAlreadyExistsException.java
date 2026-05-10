package cl.fuelonline.finance.application.exception;

public class CardProductAlreadyExistsException extends RuntimeException {
    public CardProductAlreadyExistsException(String mensaje) {
        super(mensaje);
    }
}
