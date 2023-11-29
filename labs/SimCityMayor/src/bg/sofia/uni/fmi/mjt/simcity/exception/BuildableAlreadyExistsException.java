package bg.sofia.uni.fmi.mjt.simcity.exception;

public class BuildableAlreadyExistsException extends RuntimeException {
    public BuildableAlreadyExistsException() {
        super("Buildable already exist");
    }

    public BuildableAlreadyExistsException(Throwable cause) {
        super("Buildable already exist", cause);
    }
}
