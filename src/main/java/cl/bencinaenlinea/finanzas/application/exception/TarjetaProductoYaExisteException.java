package cl.bencinaenlinea.finanzas.application.exception;

public class TarjetaProductoYaExisteException extends RuntimeException {
    public TarjetaProductoYaExisteException(String mensaje) {
        super(mensaje);
    }
}
