package bg.sofia.uni.fmi.mjt.simcity.exception;

public class InsufficientPlotAreaException extends RuntimeException {
    public InsufficientPlotAreaException() {
        super("Insufficient plot area");
    }

    public InsufficientPlotAreaException(Throwable cause) {
        super("Insufficient plot area", cause);
    }
}
