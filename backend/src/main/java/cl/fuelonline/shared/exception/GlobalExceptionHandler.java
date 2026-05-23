package cl.fuelonline.shared.exception;

import cl.fuelonline.station.application.exception.StationAlreadyExistsException;
import cl.fuelonline.finance.application.exception.BankAlreadyExistsException;
import cl.fuelonline.finance.application.exception.CardProductAlreadyExistsException;
import cl.fuelonline.transaction.application.exception.InvalidTransactionException;
import cl.fuelonline.user.application.exception.UserAlreadyExistsException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
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
            UserAlreadyExistsException.class,
            StationAlreadyExistsException.class,
            BankAlreadyExistsException.class,
            CardProductAlreadyExistsException.class
    })
    public ResponseEntity<ApiError> conflict(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError(Instant.now(), 409, "Conflict", ex.getMessage()));
    }

    @ExceptionHandler({
            InvalidTransactionException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiError> badRequest(RuntimeException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiError(Instant.now(), 400, "Bad Request", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex) {
        var detail = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        f -> f.getField(),
                        f -> f.getDefaultMessage() == null ? "invalid" : f.getDefaultMessage(),
                        (a, b) -> a));
        return ResponseEntity.badRequest()
                .body(new ApiError(Instant.now(), 400, "Validation Failed", detail));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> constraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiError(Instant.now(), 400, "Validation Failed", ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> dataIntegrity(DataIntegrityViolationException ex) {
        String cause = ex.getMostSpecificCause().getMessage();
        String detail = cause != null && cause.contains("Column")
                ? cause.split("\\[")[0].trim()
                : "Database constraint violation";
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ApiError(Instant.now(), 422, "Data Integrity Violation", detail));
    }
}
