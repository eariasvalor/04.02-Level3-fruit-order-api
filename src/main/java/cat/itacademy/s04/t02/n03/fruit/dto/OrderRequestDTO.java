package cat.itacademy.s04.t02.n03.fruit.dto;

import cat.itacademy.s04.t02.n03.fruit.validation.FutureDate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

    @NotBlank(message = "Client name is required and cannot be empty")
    private String clientName;

    @NotNull(message = "Delivery date is required")
    @FutureDate
    private LocalDate deliveryDate;

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<OrderItemDTO> items;
}
