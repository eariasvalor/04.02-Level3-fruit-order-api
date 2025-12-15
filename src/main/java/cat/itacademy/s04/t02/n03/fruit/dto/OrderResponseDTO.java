package cat.itacademy.s04.t02.n03.fruit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private String id;
    private String clientName;
    private LocalDate deliveryDate;
    private List<OrderItemDTO> items;
}