package cl.bencinaenlinea.shared.exception;

import cl.bencinaenlinea.bencinera.application.exception.BencineraYaExisteException;
import cl.bencinaenlinea.finanzas.application.exception.BancoYaExisteException;
import cl.bencinaenlinea.finanzas.application.exception.TarjetaProductoYaExisteException;
import cl.bencinaenlinea.transaccion.application.exception.CalificacionYaExisteException;
import cl.bencinaenlinea.transaccion.application.exception.FavoritoYaExisteException;
import cl.bencinaenlinea.transaccion.application.exception.TransaccionInvalidaException;
import cl.bencinaenlinea.usuario.application.exception.UsuarioYaExisteException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ApiError(Instant timestamp, int status, String error, Object detail) {}

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> notFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError(Instant.now(), 404, "Not Found", ex.getMessage()));
    }

    @ExceptionHandler({
            UsuarioYaExisteException.class,
            BencineraYaExisteException.class,
            BancoYaExisteException.class,
            TarjetaProductoYaExisteException.class,
            CalificacionYaExisteException.class,
            FavoritoYaExisteException.class
    })
    public ResponseEntity<ApiError> conflict(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError(Instant.now(), 409, "Conflict", ex.getMessage()));
    }

    @ExceptionHandler({
            TransaccionInvalidaException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiError> badRequest(RuntimeException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiError(Instant.now(), 400, "Bad Request", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex) {
        var detalle = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        f -> f.getField(),
                        f -> f.getDefaultMessage() == null ? "invalido" : f.getDefaultMessage(),
                        (a, b) -> a));
        return ResponseEntity.badRequest()
                .body(new ApiError(Instant.now(), 400, "Validation Failed", detalle));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> constraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiError(Instant.now(), 400, "Validation Failed", ex.getMessage()));
    }
}
