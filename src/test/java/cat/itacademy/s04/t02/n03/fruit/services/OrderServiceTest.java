package cat.itacademy.s04.t02.n03.fruit.services;

import cat.itacademy.s04.t02.n03.fruit.dto.OrderItemDTO;
import cat.itacademy.s04.t02.n03.fruit.dto.OrderRequestDTO;
import cat.itacademy.s04.t02.n03.fruit.dto.OrderResponseDTO;
import cat.itacademy.s04.t02.n03.fruit.exception.OrderNotFoundException;
import cat.itacademy.s04.t02.n03.fruit.mapper.OrderMapper;
import cat.itacademy.s04.t02.n03.fruit.model.Order;
import cat.itacademy.s04.t02.n03.fruit.model.OrderItem;
import cat.itacademy.s04.t02.n03.fruit.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order Service Unit Tests - Create Order")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequestDTO validOrderRequest;
    private Order mockOrder;
    private Order savedOrder;
    private OrderResponseDTO mockResponseDTO;

    @BeforeEach
    void setUp() {
        validOrderRequest = createValidOrderRequest();

        mockOrder = new Order();
        mockOrder.setClientName("John Doe");
        mockOrder.setDeliveryDate(LocalDate.now().plusDays(1));
        mockOrder.setItems(List.of(
                new OrderItem("Apple", 5),
                new OrderItem("Banana", 3)
        ));

        savedOrder = new Order();
        savedOrder.setId("generated-id-123");
        savedOrder.setClientName("John Doe");
        savedOrder.setDeliveryDate(LocalDate.now().plusDays(1));
        savedOrder.setItems(List.of(
                new OrderItem("Apple", 5),
                new OrderItem("Banana", 3)
        ));

        mockResponseDTO = new OrderResponseDTO();
        mockResponseDTO.setId("generated-id-123");
        mockResponseDTO.setClientName("John Doe");
        mockResponseDTO.setDeliveryDate(LocalDate.now().plusDays(1));
        mockResponseDTO.setItems(List.of(
                new OrderItemDTO("Apple", 5),
                new OrderItemDTO("Banana", 3)
        ));
    }

    @Test
    @DisplayName("createOrder with valid data saves to repository")
    void testCreateOrder_WithValidData_SavesToRepository() {
        when(orderMapper.toEntity(validOrderRequest)).thenReturn(mockOrder);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.toResponseDTO(savedOrder)).thenReturn(mockResponseDTO);

        OrderResponseDTO result = orderService.createOrder(validOrderRequest);

        verify(orderRepository, times(1)).save(any(Order.class));
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("createOrder with valid data returns saved order")
    void testCreateOrder_WithValidData_ReturnsSavedOrder() {
        when(orderMapper.toEntity(validOrderRequest)).thenReturn(mockOrder);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.toResponseDTO(savedOrder)).thenReturn(mockResponseDTO);

        OrderResponseDTO result = orderService.createOrder(validOrderRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("generated-id-123");
        assertThat(result.getClientName()).isEqualTo("John Doe");
        assertThat(result.getDeliveryDate()).isEqualTo(LocalDate.now().plusDays(1));
        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getItems().get(0).getFruitName()).isEqualTo("Apple");
        assertThat(result.getItems().get(0).getQuantityInKilos()).isEqualTo(5);
    }

    @Test
    @DisplayName("createOrder calls repository.save() exactly once")
    void testCreateOrder_CallsRepositorySaveOnce() {
        when(orderMapper.toEntity(validOrderRequest)).thenReturn(mockOrder);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.toResponseDTO(savedOrder)).thenReturn(mockResponseDTO);

        orderService.createOrder(validOrderRequest);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());

        Order capturedOrder = orderCaptor.getValue();
        assertThat(capturedOrder.getClientName()).isEqualTo("John Doe");
        assertThat(capturedOrder.getDeliveryDate()).isEqualTo(LocalDate.now().plusDays(1));
        assertThat(capturedOrder.getItems()).hasSize(2);
    }

    @Test
    @DisplayName("getAllOrders returns empty list when repository is empty")
    void testGetAllOrders_WithEmptyRepository_ReturnsEmptyList() {
        when(orderRepository.findAll()).thenReturn(List.of());

        List<OrderResponseDTO> result = orderService.getAllOrders();

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllOrders returns all orders from repository")
    void testGetAllOrders_WithMultipleOrders_ReturnsAllOrders() {
        Order order1 = new Order();
        order1.setId("id-1");
        order1.setClientName("John Doe");
        order1.setDeliveryDate(LocalDate.now().plusDays(1));
        order1.setItems(List.of(new OrderItem("Apple", 5)));

        Order order2 = new Order();
        order2.setId("id-2");
        order2.setClientName("Jane Smith");
        order2.setDeliveryDate(LocalDate.now().plusDays(2));
        order2.setItems(List.of(new OrderItem("Banana", 3)));

        OrderResponseDTO dto1 = new OrderResponseDTO();
        dto1.setId("id-1");
        dto1.setClientName("John Doe");

        OrderResponseDTO dto2 = new OrderResponseDTO();
        dto2.setId("id-2");
        dto2.setClientName("Jane Smith");

        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));
        when(orderMapper.toResponseDTO(order1)).thenReturn(dto1);
        when(orderMapper.toResponseDTO(order2)).thenReturn(dto2);

        List<OrderResponseDTO> result = orderService.getAllOrders();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo("id-1");
        assertThat(result.get(1).getId()).isEqualTo("id-2");
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllOrders maps entities to DTOs correctly")
    void testGetAllOrders_MapsEntitiesToDTOs() {
        Order order = new Order();
        order.setId("id-123");
        order.setClientName("Test Client");
        order.setDeliveryDate(LocalDate.now().plusDays(1));
        order.setItems(List.of(new OrderItem("Orange", 10)));

        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId("id-123");
        dto.setClientName("Test Client");
        dto.setDeliveryDate(LocalDate.now().plusDays(1));
        dto.setItems(List.of(new OrderItemDTO("Orange", 10)));

        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(orderMapper.toResponseDTO(order)).thenReturn(dto);

        List<OrderResponseDTO> result = orderService.getAllOrders();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("id-123");
        assertThat(result.get(0).getClientName()).isEqualTo("Test Client");
        assertThat(result.get(0).getItems()).hasSize(1);
        assertThat(result.get(0).getItems().get(0).getFruitName()).isEqualTo("Orange");
        assertThat(result.get(0).getItems().get(0).getQuantityInKilos()).isEqualTo(10);

        verify(orderMapper, times(1)).toResponseDTO(order);
    }

    @Test
    @DisplayName("getOrderById with existing ID returns order DTO")
    void testGetOrderById_WithExistingId_ReturnsOrderDTO() {
        String orderId = "existing-id-123";

        Order order = new Order();
        order.setId(orderId);
        order.setClientName("John Doe");
        order.setDeliveryDate(LocalDate.now().plusDays(1));
        order.setItems(List.of(new OrderItem("Apple", 5)));

        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(orderId);
        dto.setClientName("John Doe");
        dto.setDeliveryDate(LocalDate.now().plusDays(1));
        dto.setItems(List.of(new OrderItemDTO("Apple", 5)));

        when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.of(order));
        when(orderMapper.toResponseDTO(order)).thenReturn(dto);

        OrderResponseDTO result = orderService.getOrderById(orderId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(orderId);
        assertThat(result.getClientName()).isEqualTo("John Doe");
        assertThat(result.getItems()).hasSize(1);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderMapper, times(1)).toResponseDTO(order);
    }

    @Test
    @DisplayName("getOrderById with non-existing ID throws OrderNotFoundException")
    void testGetOrderById_WithNonExistingId_ThrowsException() {
        String nonExistingId = "non-existing-id";
        when(orderRepository.findById(nonExistingId)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(nonExistingId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("Order not found with id: " + nonExistingId);

        verify(orderRepository, times(1)).findById(nonExistingId);
        verify(orderMapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("getOrderById calls repository.findById() once")
    void testGetOrderById_CallsRepositoryFindByIdOnce() {
        String orderId = "test-id-456";

        Order order = new Order();
        order.setId(orderId);
        order.setClientName("Test Client");

        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.of(order));
        when(orderMapper.toResponseDTO(order)).thenReturn(dto);

        orderService.getOrderById(orderId);

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("updateOrder with valid ID updates and returns order")
    void testUpdateOrder_WithValidId_UpdatesAndReturnsOrder() {
        String orderId = "existing-id-123";

        Order existingOrder = new Order();
        existingOrder.setId(orderId);
        existingOrder.setClientName("Old Client");
        existingOrder.setDeliveryDate(LocalDate.now().plusDays(1));
        existingOrder.setItems(List.of(new OrderItem("Apple", 5)));

        OrderRequestDTO updateRequest = new OrderRequestDTO();
        updateRequest.setClientName("New Client");
        updateRequest.setDeliveryDate(LocalDate.now().plusDays(3));
        updateRequest.setItems(List.of(new OrderItemDTO("Orange", 10)));

        Order updatedOrder = new Order();
        updatedOrder.setId(orderId);
        updatedOrder.setClientName("New Client");
        updatedOrder.setDeliveryDate(LocalDate.now().plusDays(3));
        updatedOrder.setItems(List.of(new OrderItem("Orange", 10)));

        Order savedOrder = new Order();
        savedOrder.setId(orderId);
        savedOrder.setClientName("New Client");
        savedOrder.setDeliveryDate(LocalDate.now().plusDays(3));
        savedOrder.setItems(List.of(new OrderItem("Orange", 10)));

        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setId(orderId);
        responseDTO.setClientName("New Client");
        responseDTO.setDeliveryDate(LocalDate.now().plusDays(3));
        responseDTO.setItems(List.of(new OrderItemDTO("Orange", 10)));

        when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.of(existingOrder));
        when(orderMapper.toEntity(updateRequest)).thenReturn(updatedOrder);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.toResponseDTO(savedOrder)).thenReturn(responseDTO);

        OrderResponseDTO result = orderService.updateOrder(orderId, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(orderId);
        assertThat(result.getClientName()).isEqualTo("New Client");
        assertThat(result.getDeliveryDate()).isEqualTo(LocalDate.now().plusDays(3));
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getFruitName()).isEqualTo("Orange");

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderMapper, times(1)).toEntity(updateRequest);
        verify(orderMapper, times(1)).toResponseDTO(savedOrder);
    }

    @Test
    @DisplayName("updateOrder with non-existing ID throws OrderNotFoundException")
    void testUpdateOrder_WithNonExistingId_ThrowsException() {
        String nonExistingId = "non-existing-id";
        OrderRequestDTO updateRequest = validOrderRequest;

        when(orderRepository.findById(nonExistingId)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> orderService.updateOrder(nonExistingId, updateRequest))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("Order not found with id: " + nonExistingId);

        verify(orderRepository, times(1)).findById(nonExistingId);
        verify(orderRepository, never()).save(any());
        verify(orderMapper, never()).toEntity(any());
        verify(orderMapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("updateOrder calls repository.findById() and save()")
    void testUpdateOrder_CallsRepositoryMethods() {
        String orderId = "test-id-789";

        Order existingOrder = new Order();
        existingOrder.setId(orderId);
        existingOrder.setClientName("Old Client");

        Order updatedOrder = new Order();
        updatedOrder.setId(orderId);
        updatedOrder.setClientName("New Client");

        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setId(orderId);
        responseDTO.setClientName("New Client");

        when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.of(existingOrder));
        when(orderMapper.toEntity(validOrderRequest)).thenReturn(updatedOrder);
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);
        when(orderMapper.toResponseDTO(updatedOrder)).thenReturn(responseDTO);

        orderService.updateOrder(orderId, validOrderRequest);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(orderCaptor.capture());

        Order capturedOrder = orderCaptor.getValue();
        assertThat(capturedOrder.getId()).isEqualTo(orderId);
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