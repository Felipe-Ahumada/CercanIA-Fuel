package cl.fuelonline.transaction.application.exception;

public class TransaccionInvalidaException extends RuntimeException {
    public TransaccionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
