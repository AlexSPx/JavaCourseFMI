package bg.sofia.uni.fmi.mjt.order.server.repository;

import bg.sofia.uni.fmi.mjt.order.server.destination.Destination;
import bg.sofia.uni.fmi.mjt.order.server.order.Order;
import bg.sofia.uni.fmi.mjt.order.server.Response;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Color;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Size;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.TShirt;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MJTOrderRepository implements OrderRepository {
    private final AtomicInteger previousId = new AtomicInteger(1);
    private final Map<Integer, List<Order>> orders;

    public MJTOrderRepository() {
        this.orders = new ConcurrentHashMap<>();
    }

    @Override
    public Response request(String size, String color, String destination) {
        if (size == null || color == null || destination == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }

        TShirt tShirt = new TShirt(Size.of(size), Color.of(color));
        Destination dest = Destination.of(destination);

        if (tShirt.size() == Size.UNKNOWN ||
                tShirt.color() == Color.UNKNOWN ||
                dest == Destination.UNKNOWN) {
            Order order = new Order(-1, tShirt, dest);
            List<Order> invalidOrders = orders.get(-1);

            if (invalidOrders == null) {
                List<Order> invalids = new CopyOnWriteArrayList<>();
                invalids.add(order);
                orders.put(-1, invalids);
            } else {
                invalidOrders.add(order);
            }
            return Response.decline(order.generateInfo());
        }

        Order order = new Order(previousId.getAndIncrement(), tShirt, dest);

        orders.put(order.id(), List.of(order));

        return Response.create(order.id());
    }

    @Override
    public Response getOrderById(int id) {
        List<Order> order = orders.get(id);

        if (order == null) {
            return Response.notFound(id);
        }

        return Response.ok(order);
    }

    @Override
    public Response getAllOrders() {
        return Response.ok(
                orders.values()
                        .stream()
                        .flatMap(List::stream)
                        .toList()
        );
    }

    @Override
    public Response getAllSuccessfulOrders() {
        return Response.ok(
                orders.values().stream()
                        .flatMap(List::stream)
                        .filter(order -> order.id() != -1)
                        .toList()
        );
    }
}
