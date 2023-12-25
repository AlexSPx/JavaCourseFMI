package bg.sofia.uni.fmi.mjt.order.server;

import bg.sofia.uni.fmi.mjt.order.server.repository.OrderRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RequestHandler implements Runnable {
    private final Socket socket;
    private final OrderRepository repository;

    public RequestHandler(Socket socket, OrderRepository repository) {
        this.socket = socket;
        this.repository = repository;
    }

    private Response getResponse(String[] commandTokens) {
        int parameter = 1;
        return switch (commandTokens[0]) {
            case "request" -> repository
                    .request(commandTokens[parameter++].split("=")[1],
                            commandTokens[parameter++].split("=")[1],
                            commandTokens[parameter++].split("=")[1]);
            case "get" -> switch (commandTokens[1]) {
                case "all" -> repository.getAllOrders();
                case "all-successful" -> repository.getAllSuccessfulOrders();
                case "my-order" -> repository
                        .getOrderById(Integer.parseInt(commandTokens[2].split("=")[1]));
                default -> null;
            };
            default -> null;
        };
    }

    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            while (true) {
                String[] commandTokens = in.readLine().split("\\s+");

                if (commandTokens[0].equals("disconnect")) {
                    break;
                }

                Response response = getResponse(commandTokens);

                if (response == null) {
                    out.println("Unknown command");
                } else {
                    out.println(response);
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
