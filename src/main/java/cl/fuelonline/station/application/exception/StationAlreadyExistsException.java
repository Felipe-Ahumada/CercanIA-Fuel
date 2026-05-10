package cl.fuelonline.station.application.exception;

public class StationAlreadyExistsException extends RuntimeException {
    public StationAlreadyExistsException(String message) {
        super(message);
    }
}
