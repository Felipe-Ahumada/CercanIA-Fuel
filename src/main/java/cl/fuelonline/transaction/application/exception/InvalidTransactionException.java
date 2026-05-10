package cl.fuelonline.transaction.application.exception;

public class InvalidTransactionException extends RuntimeException {
    public InvalidTransactionException(String mensaje) {
        super(mensaje);
    }
}
