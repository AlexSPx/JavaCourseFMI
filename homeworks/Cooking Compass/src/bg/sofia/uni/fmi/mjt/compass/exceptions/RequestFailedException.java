package bg.sofia.uni.fmi.mjt.compass.exceptions;

public class RequestFailedException extends Exception {
    public RequestFailedException(String message) {
        super(message);
    }

    public RequestFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
