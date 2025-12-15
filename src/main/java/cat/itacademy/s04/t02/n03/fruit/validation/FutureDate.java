package cat.itacademy.s04.t02.n03.fruit.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FutureDateValidator.class)
public @interface FutureDate {

    String message() default "Delivery date must be at least tomorrow";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
