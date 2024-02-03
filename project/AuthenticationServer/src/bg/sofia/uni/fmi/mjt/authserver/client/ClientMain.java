package bg.sofia.uni.fmi.mjt.authserver.client;

import bg.sofia.uni.fmi.mjt.authserver.file.DirCreator;
import bg.sofia.uni.fmi.mjt.authserver.logger.crash.CrashLogger;
import bg.sofia.uni.fmi.mjt.authserver.logger.crash.ExceptionLogger;

public class ClientMain {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    private static final String LOGS_DIR = "client/logs/";

    public static void main(String[] args) {
        DirCreator.createDirectory(LOGS_DIR);

        ExceptionLogger logger = new CrashLogger(LOGS_DIR);

        AuthClient client = new AuthClient(logger, HOST, PORT);
        client.start();
    }
}
