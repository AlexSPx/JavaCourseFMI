package bg.sofia.uni.fmi.mjt.compass.exceptions;

public class MissingParametersException extends Exception {
    public MissingParametersException(String message) {
        super(message);
    }

    public MissingParametersException(String message, Throwable cause) {
        super(message, cause);
    }
}
