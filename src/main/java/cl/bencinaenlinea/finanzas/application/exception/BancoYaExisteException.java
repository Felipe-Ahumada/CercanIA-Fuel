package cl.bencinaenlinea.finanzas.application.exception;

public class BancoYaExisteException extends RuntimeException {
    public BancoYaExisteException(String mensaje) {
        super(mensaje);
    }
}
