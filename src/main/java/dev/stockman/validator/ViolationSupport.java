package dev.stockman.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;

import java.util.Collection;
import java.util.Optional;
import java.util.Comparator;

public interface ViolationSupport {
    Collection<Violation> violations();
    record Violation(
            String path,
            String message,
            Object invalidValue
    ) {

        public static Comparator<Violation> getComparator() {
            return Comparator
                    .comparing(Violation::path, Comparator.nullsFirst(Comparator.naturalOrder()))
                    .thenComparing(Violation::message, Comparator.nullsFirst(Comparator.naturalOrder()));
        }

        Violation(ConstraintViolation<?> constraintViolation) {
            this(
                    Optional.ofNullable(constraintViolation.getPropertyPath()).map(Path::toString).orElse(null),
                    constraintViolation.getMessage(),
                    constraintViolation.getInvalidValue()
            );
        }
    }
}
