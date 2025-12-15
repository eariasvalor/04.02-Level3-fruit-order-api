package cat.itacademy.s04.t02.n03.fruit.controllers;

import cat.itacademy.s04.t02.n03.fruit.config.BaseIntegrationTest;
import cat.itacademy.s04.t02.n03.fruit.dto.OrderItemDTO;
import cat.itacademy.s04.t02.n03.fruit.dto.OrderRequestDTO;
import cat.itacademy.s04.t02.n03.fruit.dto.OrderResponseDTO;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Test
    @DisplayName("GET /orders returns 200 OK with empty list when no orders exist")
    void testGetAllOrders_WithNoOrders_ReturnsEmptyList() throws Exception {

        mockMvc.perform(get("/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /orders returns 200 OK with list of all orders")
    void testGetAllOrders_WithMultipleOrders_ReturnsAllOrders() throws Exception {
        OrderRequestDTO order1 = createValidOrderRequest();
        OrderRequestDTO order2 = createValidOrderRequest();
        order2.setClientName("Jane Smith");
        order2.setDeliveryDate(LocalDate.now().plusDays(2));

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order1)));

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order2)));

        mockMvc.perform(get("/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].clientName").exists())
                .andExpect(jsonPath("$[1].clientName").exists());
    }

    @Test
    @DisplayName("GET /orders returns correct order data")
    void testGetAllOrders_ReturnsCorrectOrderData() throws Exception {
        OrderRequestDTO orderRequest = createValidOrderRequest();

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)));

        mockMvc.perform(get("/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].clientName").value("John Doe"))
                .andExpect(jsonPath("$[0].deliveryDate").value(LocalDate.now().plusDays(1).toString()))
                .andExpect(jsonPath("$[0].items").isArray())
                .andExpect(jsonPath("$[0].items", hasSize(2)))
                .andExpect(jsonPath("$[0].items[0].fruitName").value("Apple"))
                .andExpect(jsonPath("$[0].items[0].quantityInKilos").value(5));
    }

    @Test
    @DisplayName("GET /orders/{id} with existing ID returns 200 OK")
    void testGetOrderById_WithExistingId_Returns200() throws Exception {
        OrderRequestDTO orderRequest = createValidOrderRequest();

        String responseBody = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        OrderResponseDTO createdOrder = objectMapper.readValue(responseBody, OrderResponseDTO.class);
        String orderId = createdOrder.getId();

        mockMvc.perform(get("/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.clientName").exists())
                .andExpect(jsonPath("$.deliveryDate").exists())
                .andExpect(jsonPath("$.items").exists());
    }

    @Test
    @DisplayName("GET /orders/{id} with existing ID returns correct order data")
    void testGetOrderById_WithExistingId_ReturnsCorrectData() throws Exception {
        OrderRequestDTO orderRequest = createValidOrderRequest();

        String responseBody = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        OrderResponseDTO createdOrder = objectMapper.readValue(responseBody, OrderResponseDTO.class);
        String orderId = createdOrder.getId();

        mockMvc.perform(get("/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.clientName").value("John Doe"))
                .andExpect(jsonPath("$.deliveryDate").value(LocalDate.now().plusDays(1).toString()))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].fruitName").value("Apple"))
                .andExpect(jsonPath("$.items[0].quantityInKilos").value(5));
    }

    @Test
    @DisplayName("GET /orders/{id} with non-existing ID returns 404 Not Found")
    void testGetOrderById_WithNonExistingId_Returns404() throws Exception {
        String nonExistingId = "507f1f77bcf86cd799439011";
        mockMvc.perform(get("/orders/{id}", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /orders/{id} with invalid ID format returns 404 Not Found")
    void testGetOrderById_WithInvalidIdFormat_Returns404() throws Exception {
        String invalidId = "invalid-id-123";

        mockMvc.perform(get("/orders/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /orders/{id} with valid data returns 200 OK")
    void testUpdateOrder_WithValidData_Returns200() throws Exception {
        OrderRequestDTO originalOrder = createValidOrderRequest();

        String responseBody = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(originalOrder)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        OrderResponseDTO createdOrder = objectMapper.readValue(responseBody, OrderResponseDTO.class);
        String orderId = createdOrder.getId();

        OrderRequestDTO updatedOrder = createValidOrderRequest();
        updatedOrder.setClientName("Jane Smith Updated");
        updatedOrder.setDeliveryDate(LocalDate.now().plusDays(3));

        mockMvc.perform(put("/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.clientName").value("Jane Smith Updated"))
                .andExpect(jsonPath("$.deliveryDate").value(LocalDate.now().plusDays(3).toString()));
    }

    @Test
    @DisplayName("PUT /orders/{id} returns updated order data")
    void testUpdateOrder_ReturnsUpdatedData() throws Exception {
        OrderRequestDTO originalOrder = createValidOrderRequest();

        String responseBody = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(originalOrder)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        OrderResponseDTO createdOrder = objectMapper.readValue(responseBody, OrderResponseDTO.class);
        String orderId = createdOrder.getId();

        OrderItemDTO newItem = new OrderItemDTO();
        newItem.setFruitName("Orange");
        newItem.setQuantityInKilos(10);

        OrderRequestDTO updatedOrder = new OrderRequestDTO();
        updatedOrder.setClientName("Updated Client");
        updatedOrder.setDeliveryDate(LocalDate.now().plusDays(5));
        updatedOrder.setItems(List.of(newItem));

        mockMvc.perform(put("/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientName").value("Updated Client"))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].fruitName").value("Orange"))
                .andExpect(jsonPath("$.items[0].quantityInKilos").value(10));
    }

    @Test
    @DisplayName("PUT /orders/{id} with non-existing ID returns 404 Not Found")
    void testUpdateOrder_WithNonExistingId_Returns404() throws Exception {
        String nonExistingId = "507f1f77bcf86cd799439011";
        OrderRequestDTO updateRequest = createValidOrderRequest();

        mockMvc.perform(put("/orders/{id}", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("PUT /orders/{id} with invalid clientName returns 400 Bad Request")
    void testUpdateOrder_WithInvalidClientName_Returns400() throws Exception {
        OrderRequestDTO originalOrder = createValidOrderRequest();

        String responseBody = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(originalOrder)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        OrderResponseDTO createdOrder = objectMapper.readValue(responseBody, OrderResponseDTO.class);
        String orderId = createdOrder.getId();

        OrderRequestDTO invalidUpdate = createValidOrderRequest();
        invalidUpdate.setClientName("");

        mockMvc.perform(put("/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("PUT /orders/{id} with past date returns 400 Bad Request")
    void testUpdateOrder_WithPastDate_Returns400() throws Exception {
        OrderRequestDTO originalOrder = createValidOrderRequest();

        String responseBody = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(originalOrder)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        OrderResponseDTO createdOrder = objectMapper.readValue(responseBody, OrderResponseDTO.class);
        String orderId = createdOrder.getId();

        OrderRequestDTO invalidUpdate = createValidOrderRequest();
        invalidUpdate.setDeliveryDate(LocalDate.now().minusDays(1));

        mockMvc.perform(put("/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("PUT /orders/{id} with today's date returns 400 Bad Request")
    void testUpdateOrder_WithTodayDate_Returns400() throws Exception {
        OrderRequestDTO originalOrder = createValidOrderRequest();

        String responseBody = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(originalOrder)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        OrderResponseDTO createdOrder = objectMapper.readValue(responseBody, OrderResponseDTO.class);
        String orderId = createdOrder.getId();

        OrderRequestDTO invalidUpdate = createValidOrderRequest();
        invalidUpdate.setDeliveryDate(LocalDate.now());

        mockMvc.perform(put("/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("PUT /orders/{id} with empty items returns 400 Bad Request")
    void testUpdateOrder_WithEmptyItems_Returns400() throws Exception {
        OrderRequestDTO originalOrder = createValidOrderRequest();

        String responseBody = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(originalOrder)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        OrderResponseDTO createdOrder = objectMapper.readValue(responseBody, OrderResponseDTO.class);
        String orderId = createdOrder.getId();

        OrderRequestDTO invalidUpdate = createValidOrderRequest();
        invalidUpdate.setItems(new ArrayList<>());

        mockMvc.perform(put("/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("PUT /orders/{id} with invalid item data returns 400 Bad Request")
    void testUpdateOrder_WithInvalidItemData_Returns400() throws Exception {
        OrderRequestDTO originalOrder = createValidOrderRequest();

        String responseBody = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(originalOrder)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        OrderResponseDTO createdOrder = objectMapper.readValue(responseBody, OrderResponseDTO.class);
        String orderId = createdOrder.getId();

        OrderRequestDTO invalidUpdate = createValidOrderRequest();
        invalidUpdate.getItems().get(0).setQuantityInKilos(-5);

        mockMvc.perform(put("/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
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
