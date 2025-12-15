package cat.itacademy.s04.t02.n03.fruit.mapper;

import cat.itacademy.s04.t02.n03.fruit.dto.OrderItemDTO;
import cat.itacademy.s04.t02.n03.fruit.dto.OrderRequestDTO;
import cat.itacademy.s04.t02.n03.fruit.dto.OrderResponseDTO;
import cat.itacademy.s04.t02.n03.fruit.model.Order;
import cat.itacademy.s04.t02.n03.fruit.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public Order toEntity(OrderRequestDTO dto) {
        Order order = new Order();
        order.setClientName(dto.getClientName());
        order.setDeliveryDate(dto.getDeliveryDate());
        order.setItems(toOrderItemList(dto.getItems()));
        return order;
    }

    public OrderResponseDTO toResponseDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setClientName(order.getClientName());
        dto.setDeliveryDate(order.getDeliveryDate());
        dto.setItems(toOrderItemDTOList(order.getItems()));
        return dto;
    }

    private List<OrderItem> toOrderItemList(List<OrderItemDTO> dtoList) {
        return dtoList.stream()
                .map(dto -> new OrderItem(dto.getFruitName(), dto.getQuantityInKilos()))
                .collect(Collectors.toList());
    }

    private List<OrderItemDTO> toOrderItemDTOList(List<OrderItem> itemList) {
        return itemList.stream()
                .map(item -> new OrderItemDTO(item.getFruitName(), item.getQuantityInKilos()))
                .collect(Collectors.toList());
    }
}
