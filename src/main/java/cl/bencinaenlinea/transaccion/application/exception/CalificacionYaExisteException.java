package cl.bencinaenlinea.transaccion.application.exception;

public class CalificacionYaExisteException extends RuntimeException {
    public CalificacionYaExisteException(String mensaje) {
        super(mensaje);
    }
}
