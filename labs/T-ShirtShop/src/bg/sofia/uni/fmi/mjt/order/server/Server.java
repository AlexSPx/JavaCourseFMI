package bg.sofia.uni.fmi.mjt.order.server;

import bg.sofia.uni.fmi.mjt.order.server.repository.MJTOrderRepository;
import bg.sofia.uni.fmi.mjt.order.server.repository.OrderRepository;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int SERVER_PORT = 8080;
    private static final int MAX_EXECUTOR_THREADS = 10;

    public void start() {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS);

        OrderRepository repository = new MJTOrderRepository();

        Thread.currentThread().setName("Server Thread");

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {

            System.out.println("TShirt server running on port, " + serverSocket.getLocalPort());

            Socket clientSocket;

            while (true) {
                clientSocket = serverSocket.accept();

                System.out.println("Accepted connection request from client " + clientSocket.getInetAddress());

                RequestHandler requestHandler = new RequestHandler(clientSocket, repository);

                executor.execute(requestHandler);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
