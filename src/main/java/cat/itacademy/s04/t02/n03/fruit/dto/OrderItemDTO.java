package cat.itacademy.s04.t02.n03.fruit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {

    @NotBlank(message = "Fruit name is required and cannot be empty")
    private String fruitName;

    @Positive(message = "Quantity must be greater than zero")
    private int quantityInKilos;
}
