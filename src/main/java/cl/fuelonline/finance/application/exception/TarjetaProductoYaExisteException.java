package cl.fuelonline.finance.application.exception;

public class TarjetaProductoYaExisteException extends RuntimeException {
    public TarjetaProductoYaExisteException(String mensaje) {
        super(mensaje);
    }
}
