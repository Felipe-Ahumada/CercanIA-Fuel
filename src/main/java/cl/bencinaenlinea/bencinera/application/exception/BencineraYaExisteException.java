package cl.bencinaenlinea.bencinera.application.exception;

public class BencineraYaExisteException extends RuntimeException {
    public BencineraYaExisteException(String mensaje) {
        super(mensaje);
    }
}
