package cat.itacademy.s04.t02.n03.fruit.services;

import cat.itacademy.s04.t02.n03.fruit.dto.OrderRequestDTO;
import cat.itacademy.s04.t02.n03.fruit.dto.OrderResponseDTO;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO);
}