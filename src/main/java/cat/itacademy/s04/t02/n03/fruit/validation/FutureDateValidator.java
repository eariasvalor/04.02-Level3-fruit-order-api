package cat.itacademy.s04.t02.n03.fruit.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class FutureDateValidator implements ConstraintValidator<FutureDate, LocalDate> {

    @Override
    public void initialize(FutureDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null) {
            return true;
        }

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return !date.isBefore(tomorrow);
    }
}
