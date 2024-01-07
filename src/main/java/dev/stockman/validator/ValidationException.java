package dev.stockman.validator;

import java.util.ArrayList;
import java.util.Collection;

public class ValidationException extends RuntimeException implements ViolationSupport {

    private final Collection<Violation> violations;

    ValidationException(String message, Collection<Violation> violations) {
        super(message);
        this.violations = violations;
    }

    @Override
    public Collection<Violation> violations() {
        return new ArrayList<>(violations);
    }
}
