package cl.fuelonline.finance.application.exception;

public class BancoYaExisteException extends RuntimeException {
    public BancoYaExisteException(String mensaje) {
        super(mensaje);
    }
}
