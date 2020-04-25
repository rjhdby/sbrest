package sb.test.rest.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import sb.test.rest.domain.Employee;
import sb.test.rest.domain.EmployeeRole;

import java.util.Date;
import java.util.stream.Stream;

class EmployeeValidatorTest {

    private final EmployeeValidator validator = new EmployeeValidator();

    @ParameterizedTest
    @MethodSource("isAdminEmployeeProvider")
    void isAdmin(Employee employee, boolean result) {
        Assertions.assertEquals(validator.isAdmin(employee), result);
    }

    @ParameterizedTest
    @MethodSource("newEmployeeProvider")
    void isValidNewEmployee(Employee employee, boolean result) {
        Assertions.assertEquals(validator.isValidNewEmployee(employee), result);
    }

    @ParameterizedTest
    @MethodSource("selfUpdateEmployeeProvider")
    void isValidSelfUpdatedEmployee(Employee employee, boolean result) {
        Assertions.assertEquals(validator.isValidSelfUpdatedEmployee(employee), result);
    }

    private static Stream<Arguments> isAdminEmployeeProvider() {
        return Stream.of(
            Arguments.of(new Employee(1, "n", "p", "l", "p", new Date(), EmployeeRole.ADMIN), true),

            Arguments.of(new Employee(1, "n", "p", "l", "p", new Date(), EmployeeRole.USER), false),
            Arguments.of(new Employee(1, "n", "p", "l", "p", new Date(), null), false)
        );
    }

    private static Stream<Arguments> newEmployeeProvider() {
        return Stream.of(
            Arguments.of(new Employee(null, "n", "p", "l", "p", new Date(), EmployeeRole.USER), true),

            Arguments.of(new Employee(1, "n", "p", "l", "p", new Date(), EmployeeRole.USER), false),
            Arguments.of(new Employee(null, null, "p", "l", "p", new Date(), EmployeeRole.USER), false),
            Arguments.of(new Employee(null, "n", null, "l", "p", new Date(), EmployeeRole.USER), false),
            Arguments.of(new Employee(null, "n", "p", null, "p", new Date(), EmployeeRole.USER), false),
            Arguments.of(new Employee(null, "n", "p", "l", null, new Date(), EmployeeRole.USER), false),
            Arguments.of(new Employee(null, "n", "p", "l", "p", null, EmployeeRole.USER), false),
            Arguments.of(new Employee(null, "n", "p", "l", "p", new Date(), null), false)
        );
    }

    private static Stream<Arguments> selfUpdateEmployeeProvider() {
        return Stream.of(
            Arguments.of(new Employee(null, "n", "p", null, null, new Date(), null), true),
            Arguments.of(new Employee(null, null, "p", null, null, new Date(), null), true),
            Arguments.of(new Employee(null, "n", null, null, null, new Date(), null), true),
            Arguments.of(new Employee(null, "n", null, null, null, null, null), true),
            Arguments.of(new Employee(1, "n", "p", null, null, new Date(), null), true),

            Arguments.of(new Employee(null, "n", "p", "l", null, new Date(), null), false),
            Arguments.of(new Employee(null, "n", "p", null, "p", new Date(), null), false),
            Arguments.of(new Employee(null, "n", "p", null, null, new Date(), EmployeeRole.USER), false)
        );
    }
}
