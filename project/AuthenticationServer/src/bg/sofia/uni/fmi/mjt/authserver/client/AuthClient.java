package bg.sofia.uni.fmi.mjt.authserver.client;

import bg.sofia.uni.fmi.mjt.authserver.logger.crash.ExceptionLogger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class AuthClient {
    private final int serverPort;
    private final String serverHost;
    private static final int BUFFER_SIZE = 1024;

    private ByteBuffer buffer;

    private ExceptionLogger logger;

    public AuthClient(ExceptionLogger logger, String serverHost, int serverPort) {
        this.logger = logger;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public void start() {
        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
            socketChannel.connect(new InetSocketAddress(serverHost, serverPort));
            System.out.println("Connected to the server.");

            while (true) {
                System.out.print("Enter command: ");
                String message = scanner.nextLine();

                if (message.equals("quit")) {
                    break;
                }

                sendMessage(socketChannel, message);
            }
        } catch (Exception e) {
            String file = logger.log(e);
            System.out.printf("""
                            Something unexpected has happened.
                            Please provide the logs from %s to an administrator
                            %n""",
                    file);
        }
    }

    private void sendMessage(SocketChannel channel, String message) {
        try {
            if (message.isBlank()) {
                System.out.println("Enter a valid message");
                return;
            }

            System.out.println("Sending message <" + message + "> to the server...");

            buffer.clear();
            buffer.put(message.getBytes());
            buffer.flip();
            channel.write(buffer);

            buffer.clear();
            channel.read(buffer);
            buffer.flip();

            byte[] byteArray = new byte[buffer.remaining()];
            buffer.get(byteArray);
            String reply = new String(byteArray, StandardCharsets.UTF_8);

            System.out.println(reply);
        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the network communication", e);
        }
    }
}
