package bg.sofia.uni.fmi.mjt.order.server.repository;

import bg.sofia.uni.fmi.mjt.order.server.repository.exception.OrderNotFoundException;
import bg.sofia.uni.fmi.mjt.order.server.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MJTOrderRepositoryTest {

    private MJTOrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository = new MJTOrderRepository();
    }

    @Test
    void testRequestCreated() {
        Response response = orderRepository.request("M", "BLACK", "EUROPE");
        assertEquals("{\"status\":\"CREATED\", \"additionalInfo\":\"ORDER_ID=1\"}", response.toString());
    }

    @Test
    void testRequestDeclinedSize() {
        Response response = orderRepository.request("INVALID_SIZE", "BLACK", "EUROPE");
        assertEquals("{\"status\":\"DECLINED\", \"additionalInfo\":\"invalid=size\"}", response.toString());
    }

    @Test
    void testGetOrderById() {
        Response response = orderRepository.request("M", "BLACK", "EUROPE");
        assertEquals("{\"status\":\"OK\", \"orders\":[{\"id\":\"1\", \"tShirt\":{\"size\":\"M\", \"color\":\"BLACK\"}, \"destination\":\"EUROPE\"}]}", orderRepository.getOrderById(1   ).toString());
    }

    @Test
    void testGetOrderByIdNotFound() {
        assertEquals("{\"status\":\"NOT_FOUND\", \"additionalInfo\":\"Order with id = -1 does not exist.\"}", orderRepository.getOrderById(-1).toString());
    }

    @Test
    void testGetAllOrders() {
        orderRepository.request("M", "BLACK", "EUROPE");
        orderRepository.request("S", "WHITE", "ASIA");

        Response getAllOrdersResponse = orderRepository.getAllOrders();
        assertEquals("{\"status\":\"OK\", \"orders\":[{\"id\":\"-1\", \"tShirt\":{\"size\":\"S\", \"color\":\"WHITE\"}, \"destination\":\"UNKNOWN\"}, {\"id\":\"1\", \"tShirt\":{\"size\":\"M\", \"color\":\"BLACK\"}, \"destination\":\"EUROPE\"}]}", getAllOrdersResponse.toString());
    }

    @Test
    void testGetAllSuccessfulOrders() {
        orderRepository.request("M", "BLACK", "EUROPE");
        orderRepository.request("S", "WHITE", "ASIA");
        orderRepository.request("L", "RED", "NORTH_AMERICA");

        Response getAllSuccessfulOrdersResponse = orderRepository.getAllSuccessfulOrders();
        assertEquals("{\"status\":\"OK\", \"orders\":[{\"id\":\"1\", \"tShirt\":{\"size\":\"M\", \"color\":\"BLACK\"}, \"destination\":\"EUROPE\"}, {\"id\":\"2\", \"tShirt\":{\"size\":\"L\", \"color\":\"RED\"}, \"destination\":\"NORTH_AMERICA\"}]}", getAllSuccessfulOrdersResponse.toString());
    }
}