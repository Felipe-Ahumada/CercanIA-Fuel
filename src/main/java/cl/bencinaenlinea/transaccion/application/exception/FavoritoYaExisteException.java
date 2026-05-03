package cl.bencinaenlinea.transaccion.application.exception;

public class FavoritoYaExisteException extends RuntimeException {
    public FavoritoYaExisteException(String mensaje) {
        super(mensaje);
    }
}
