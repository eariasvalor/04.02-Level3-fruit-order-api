package cat.itacademy.s04.t02.n03.fruit.controllers;

import cat.itacademy.s04.t02.n03.fruit.config.BaseIntegrationTest;
import cat.itacademy.s04.t02.n03.fruit.dto.OrderItemDTO;
import cat.itacademy.s04.t02.n03.fruit.dto.OrderRequestDTO;
import cat.itacademy.s04.t02.n03.fruit.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Order Controller Integration Tests - Create Order")
class OrderControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }


    @Test
    @DisplayName("POST /orders with valid order returns 201 Created")
    void testCreateOrder_WithValidData_Returns201() throws Exception {
        OrderRequestDTO orderRequest = createValidOrderRequest();

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.clientName").value("John Doe"))
                .andExpect(jsonPath("$.deliveryDate").value(LocalDate.now().plusDays(1).toString()))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].fruitName").value("Apple"))
                .andExpect(jsonPath("$.items[0].quantityInKilos").value(5))
                .andExpect(jsonPath("$.items[1].fruitName").value("Banana"))
                .andExpect(jsonPath("$.items[1].quantityInKilos").value(3));
    }

    @Test
    @DisplayName("POST /orders returns order with generated ID")
    void testCreateOrder_ReturnsGeneratedId() throws Exception {
        OrderRequestDTO orderRequest = createValidOrderRequest();

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }


    @Test
    @DisplayName("POST /orders with empty clientName returns 400 Bad Request")
    void testCreateOrder_WithEmptyClientName_Returns400() throws Exception {
        OrderRequestDTO orderRequest = createValidOrderRequest();
        orderRequest.setClientName("");

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /orders with null clientName returns 400 Bad Request")
    void testCreateOrder_WithNullClientName_Returns400() throws Exception {
        OrderRequestDTO orderRequest = createValidOrderRequest();
        orderRequest.setClientName(null);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }


    @Test
    @DisplayName("POST /orders with null deliveryDate returns 400 Bad Request")
    void testCreateOrder_WithNullDeliveryDate_Returns400() throws Exception {
        OrderRequestDTO orderRequest = createValidOrderRequest();
        orderRequest.setDeliveryDate(null);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /orders with past deliveryDate returns 400 Bad Request")
    void testCreateOrder_WithPastDeliveryDate_Returns400() throws Exception {
        OrderRequestDTO orderRequest = createValidOrderRequest();
        orderRequest.setDeliveryDate(LocalDate.now().minusDays(1));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /orders with today's deliveryDate returns 400 Bad Request")
    void testCreateOrder_WithTodayDeliveryDate_Returns400() throws Exception {
        OrderRequestDTO orderRequest = createValidOrderRequest();
        orderRequest.setDeliveryDate(LocalDate.now());

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }


    @Test
    @DisplayName("POST /orders with empty items list returns 400 Bad Request")
    void testCreateOrder_WithEmptyItemsList_Returns400() throws Exception {
        OrderRequestDTO orderRequest = createValidOrderRequest();
        orderRequest.setItems(new ArrayList<>());

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /orders with null items returns 400 Bad Request")
    void testCreateOrder_WithNullItems_Returns400() throws Exception {
        OrderRequestDTO orderRequest = createValidOrderRequest();
        orderRequest.setItems(null);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }


    @Test
    @DisplayName("POST /orders with item missing fruitName returns 400 Bad Request")
    void testCreateOrder_WithItemMissingFruitName_Returns400() throws Exception {
        OrderRequestDTO orderRequest = createValidOrderRequest();
        orderRequest.getItems().get(0).setFruitName("");

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /orders with item quantityInKilos = 0 returns 400 Bad Request")
    void testCreateOrder_WithZeroQuantity_Returns400() throws Exception {
        OrderRequestDTO orderRequest = createValidOrderRequest();
        orderRequest.getItems().get(0).setQuantityInKilos(0);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /orders with item quantityInKilos < 0 returns 400 Bad Request")
    void testCreateOrder_WithNegativeQuantity_Returns400() throws Exception {
        OrderRequestDTO orderRequest = createValidOrderRequest();
        orderRequest.getItems().get(0).setQuantityInKilos(-5);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }


    private OrderRequestDTO createValidOrderRequest() {
        OrderItemDTO item1 = new OrderItemDTO();
        item1.setFruitName("Apple");
        item1.setQuantityInKilos(5);

        OrderItemDTO item2 = new OrderItemDTO();
        item2.setFruitName("Banana");
        item2.setQuantityInKilos(3);

        OrderRequestDTO orderRequest = new OrderRequestDTO();
        orderRequest.setClientName("John Doe");
        orderRequest.setDeliveryDate(LocalDate.now().plusDays(1));
        orderRequest.setItems(List.of(item1, item2));

        return orderRequest;
    }
}
