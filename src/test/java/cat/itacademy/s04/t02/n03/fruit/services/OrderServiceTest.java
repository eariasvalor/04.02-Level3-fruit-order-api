package cat.itacademy.s04.t02.n03.fruit.services;

import cat.itacademy.s04.t02.n03.fruit.dto.OrderItemDTO;
import cat.itacademy.s04.t02.n03.fruit.dto.OrderRequestDTO;
import cat.itacademy.s04.t02.n03.fruit.dto.OrderResponseDTO;
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