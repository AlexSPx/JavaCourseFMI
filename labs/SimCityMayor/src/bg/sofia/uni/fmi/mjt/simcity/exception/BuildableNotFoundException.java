package bg.sofia.uni.fmi.mjt.simcity.exception;

public class BuildableNotFoundException extends RuntimeException {
    public BuildableNotFoundException() {
        super("Buildable not found");
    }

    public BuildableNotFoundException(Throwable cause) {
        super("Buildable not found", cause);
    }
}
