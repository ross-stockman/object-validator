package dev.stockman.validator;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ObjectValidatorTest {

    private static Validator hibernate;

    @BeforeAll
    public static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            hibernate = factory.getValidator();
        }
    }

    @ParameterizedTest(name = "{index} => street={0}, apartment={1}, city={2}")
    @CsvSource({
            "123 Elm Street, Unit A, Arlington",
            "321 Pine Street, , Franklin"
    })
    @DisplayName("Test valid object")
    public void testValidObject(String street, String apartment, String city) {
        ObjectValidator validator = new ObjectValidator(hibernate);
        Assertions.assertDoesNotThrow(()->validator.validate(new Address(street, apartment, city)));
    }

    @ParameterizedTest(name = "{index} => street={0}, apartment={1}, city={2}")
    @CsvSource({
            "123 Elm Street, Unit A, Arlington"
    })
    @DisplayName("Test valid object state")
    public void testValidObjectState(String street, String apartment, String city) {
        ObjectValidator validator = new ObjectValidator(hibernate);
        Assertions.assertDoesNotThrow(()->validator.validate(new Address(street, apartment, city),
                address -> Set.of()
        ));
    }

    @ParameterizedTest(name = "{index} => street={0}, apartment={1}, city={2}, path={3}, message={4}, invalidValue={5}")
    @CsvSource({
            "12345678901234567890 Elm Street, Unit A, Arlington, street, size must be between 0 and 20, 12345678901234567890 Elm Street",
            "321 Pine Street, , , city, city is required,"
    })
    @DisplayName("Test invalid object")
    public void testInvalidObject(String street, String apartment, String city, String path, String message, String invalidValue) {
        ObjectValidator validator = new ObjectValidator(hibernate);
        ValidationException e = Assertions.assertThrows(ValidationException.class,
                ()->validator.validate(new Address(street, apartment, city)));
        Assertions.assertEquals("Input is invalid!", e.getMessage());
        Assertions.assertNotNull(e.violations());
        Assertions.assertEquals(1, e.violations().size());
        Assertions.assertAll(
                () -> Assertions.assertEquals(message, new ArrayList<>(e.violations()).getFirst().message()),
                () -> Assertions.assertEquals(path, new ArrayList<>(e.violations()).getFirst().path()),
                () -> Assertions.assertEquals(invalidValue, new ArrayList<>(e.violations()).getFirst().invalidValue())
        );
    }

    @ParameterizedTest(name = "{index} => street={0}, apartment={1}, path1={2}, message1={3}, invalidValue1={4}, path2={5}, message2={6}, invalidValue2={7}")
    @CsvSource({
            "123 Elm Street, Unit A, Arlington, id, wrong id, 1, name, wrong name, 2"
    })
    @DisplayName("Test invalid object state")
    public void testValidObjectState(String street, String apartment, String city, String path1, String message1, String invalidValue1, String path2, String message2, String invalidValue2) {
        ObjectValidator validator = new ObjectValidator(hibernate);
        ValidationException e = Assertions.assertThrows(ValidationException.class,
                ()->validator.validate(new Address(street, apartment, city),
                        address -> Set.of(
                                new ViolationSupport.Violation(path1, message1, invalidValue1),
                                new ViolationSupport.Violation(path2, message2, invalidValue2))));
        Assertions.assertEquals("Input is invalid!", e.getMessage());
        Assertions.assertNotNull(e.violations());
        Assertions.assertEquals(2, e.violations().size());
        List<ViolationSupport.Violation> violations = e.violations().stream().sorted(ViolationSupport.Violation.getComparator()).toList();
        Assertions.assertAll(
                () -> Assertions.assertEquals(message1, new ArrayList<>(violations).getFirst().message()),
                () -> Assertions.assertEquals(path1, new ArrayList<>(violations).getFirst().path()),
                () -> Assertions.assertEquals(invalidValue1, new ArrayList<>(violations).getFirst().invalidValue())
        );
        Assertions.assertAll(
                () -> Assertions.assertEquals(message2, new ArrayList<>(violations).get(1).message()),
                () -> Assertions.assertEquals(path2, new ArrayList<>(violations).get(1).path()),
                () -> Assertions.assertEquals(invalidValue2, new ArrayList<>(violations).get(1).invalidValue())
        );
    }
}
