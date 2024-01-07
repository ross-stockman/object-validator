package dev.stockman.validator;

import jakarta.validation.Validator;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ObjectValidator {

    private final Validator validator;

    public ObjectValidator(Validator validator) {
        this.validator = validator;
    }

    public <T> void validate(T obj, StateValidator<T> logic) {
        var violations = validator.validate(obj).stream().map(ViolationSupport.Violation::new).collect(Collectors.toSet());
        violations.addAll(logic.apply(obj));
        if (!violations.isEmpty()) {
            throw new ValidationException("Input is invalid!", violations);
        }
    }

    public void validate(Object obj) {
        validate(obj, (o) -> Set.of());
    }

    public interface StateValidator<I> extends Function<I, Set<ViolationSupport.Violation>> { }
}
