package cat.itacademy.s04.t02.n03.fruit.services;

import cat.itacademy.s04.t02.n03.fruit.dto.OrderRequestDTO;
import cat.itacademy.s04.t02.n03.fruit.dto.OrderResponseDTO;
import cat.itacademy.s04.t02.n03.fruit.mapper.OrderMapper;
import cat.itacademy.s04.t02.n03.fruit.model.Order;
import cat.itacademy.s04.t02.n03.fruit.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        Order order = orderMapper.toEntity(orderRequestDTO);

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toResponseDTO(savedOrder);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}