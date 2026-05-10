package cl.fuelonline.transaction.application.exception;

public class CalificacionYaExisteException extends RuntimeException {
    public CalificacionYaExisteException(String mensaje) {
        super(mensaje);
    }
}
