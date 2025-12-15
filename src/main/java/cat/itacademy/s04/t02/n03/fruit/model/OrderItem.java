package cat.itacademy.s04.t02.n03.fruit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private String fruitName;
    private int quantityInKilos;
}
