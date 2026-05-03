package cl.bencinaenlinea.transaccion.application.exception;

public class TransaccionInvalidaException extends RuntimeException {
    public TransaccionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
