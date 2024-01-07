package dev.stockman.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ViolationComparatorTest {
    @ParameterizedTest(name = "{index} => path1={0}, message1={1}, invalidValue1={2}, path2={3}, message2={4}, invalidValue2={5}, expected={6}")
    @CsvSource({
            "id, invalid id, 123, name, invalid name, 321, -1",
            "name, invalid name, 321, id, invalid id, 123, 1",
            "id, invalid id, 123, id, invalid id, 321, 0",
            ", invalid id, 123, name, invalid name, 321, -1",
            "name, , 321, id, invalid id, 123, 1",
            ", invalid id, 123, , invalid name, 321, -1",
            "name, , 321, id, , 123, 1",
    })
    @DisplayName("Test comparing violations")
    public void getCompare(String path1, String message1, String invalidValue1, String path2, String message2, String invalidValue2, int expected) {
        ViolationSupport.Violation v1 = new ViolationSupport.Violation(path1, message1, invalidValue1);
        ViolationSupport.Violation v2 = new ViolationSupport.Violation(path2, message2, invalidValue2);
        int actual = ViolationSupport.Violation.getComparator().compare(v1, v2);
        int converted = Integer.compare(actual, 0);
        Assertions.assertEquals(expected, converted);
    }
}
