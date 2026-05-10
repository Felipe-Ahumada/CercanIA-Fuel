package cl.fuelonline.transaction.application.exception;

public class FavoritoYaExisteException extends RuntimeException {
    public FavoritoYaExisteException(String mensaje) {
        super(mensaje);
    }
}
