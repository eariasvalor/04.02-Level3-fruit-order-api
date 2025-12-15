package cat.itacademy.s04.t02.n03.fruit.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String id) {
        super("Order not found with id: " + id);
    }
}
