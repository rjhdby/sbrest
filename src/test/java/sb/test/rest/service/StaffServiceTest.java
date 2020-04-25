package sb.test.rest.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import sb.test.rest.domain.Employee;
import sb.test.rest.domain.EmployeeRole;

import java.util.Date;
import java.util.stream.Stream;

/*
 * Current test DB data used
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@EnableConfigurationProperties
class StaffServiceTest {
    @Autowired
    private StaffService staffService;
    @Autowired
    private PasswordUtils passwordUtils;

    @Test
    void list() {
        var one = staffService.list(PageRequest.of(0, 1)).toList();
        Assertions.assertEquals("admin", one.get(0).getLogin());

        var page1 = staffService.list(PageRequest.of(0, 10)).toList();
        Assertions.assertEquals(10, page1.size());

        var page2 = staffService.list(PageRequest.of(1, 12)).toList();
        Assertions.assertEquals(12, page2.size());

        var joinCount = Stream.concat(page1.stream(), page2.stream()).map(Employee::getLogin).distinct().count();
        Assertions.assertEquals(22, joinCount);
    }

    @Test
    void findByName() {
        var find = staffService.findByName("ва", PageRequest.of(0, 100)).toList();
        var correctCount = find.stream().filter(e -> e.getName().contains("ва")).count();
        Assertions.assertTrue(find.size() > 0);
        Assertions.assertEquals(correctCount, find.size());
    }

    @Test
    void getEmployee() {
        var employeeAdmin = staffService.getEmployee("admin");
        Assertions.assertEquals("admin", employeeAdmin.getLogin());

        Assertions.assertThrows(
            ResponseStatusException.class,
            () -> staffService.getEmployee("unknown"),
            "404 NOT_FOUND \"Employee not found\""
        );

        Assertions.assertThrows(
            ResponseStatusException.class,
            () -> staffService.getEmployee(null),
            "404 NOT_FOUND \"Employee not found\""
        );
    }

    @Test
    void addEmployee() {
        setSecurityContext("admin");

        var correctEmployee1 = new Employee(null, "new1", "p", "new1", "p", new Date(), EmployeeRole.USER);
        var correctEmployee2 = new Employee(null, "new2", "p", "new2", "p", new Date(), EmployeeRole.USER);
        var incorrectEmployee = new Employee(null, "new3", "p", "new3", null, new Date(), EmployeeRole.USER);

        var result = staffService.addEmployee(correctEmployee1);
        var saved = staffService.getEmployee(correctEmployee1.getLogin());

        Assertions.assertEquals(result, saved);

        Assertions.assertThrows(
            ResponseStatusException.class,
            () -> staffService.addEmployee(incorrectEmployee),
            "417 EXPECTATION_FAILED \"Invalid payload\""
        );

        Assertions.assertThrows(
            ResponseStatusException.class,
            () -> staffService.addEmployee(correctEmployee1),
            "409 CONFLICT \"Employee with same login already exists\""
        );

        setSecurityContext("new1");

        Assertions.assertThrows(
            ResponseStatusException.class,
            () -> staffService.addEmployee(correctEmployee2),
            "403 FORBIDDEN \"Insufficiently rights\""
        );
    }

    @Test
    void deleteEmployee() {
        setSecurityContext("admin");
        var employee = new Employee(null, "forDelete", "p", "forDelete", "p", new Date(), EmployeeRole.USER);
        staffService.addEmployee(employee);

        Assertions.assertNotNull(staffService.getEmployee("forDelete"));

        staffService.deleteEmployee("forDelete");

        Assertions.assertThrows(
            ResponseStatusException.class,
            () -> staffService.getEmployee("unknown"),
            "404 NOT_FOUND \"Employee not found\""
        );

        staffService.addEmployee(employee);

        setSecurityContext("forDelete");

        Assertions.assertThrows(
            ResponseStatusException.class,
            () -> staffService.deleteEmployee("forDelete"),
            "403 FORBIDDEN \"Insufficiently rights\""
        );
    }

    @Test
    void updateEmployee() {
        setSecurityContext("admin");
        var employee = new Employee(null, "forUpdate", "p", "forUpdate", "p", new Date(0), EmployeeRole.ADMIN);
        var fake = new Employee(null, "fake", "p", "fake", "p", new Date(0), EmployeeRole.USER);
        staffService.addEmployee(employee);
        staffService.addEmployee(fake);

        var newDate = new Date(10);
        var correctUpdate1 = new Employee(null, "forUpdate1", "p1", "forUpdate1", "p1", newDate, EmployeeRole.USER);

        var result = staffService.updateEmployee("forUpdate", correctUpdate1);

        Assertions.assertEquals("forUpdate1", result.getName());
        Assertions.assertEquals("forUpdate1", result.getLogin());
        Assertions.assertEquals("p1", result.getPosition());
        Assertions.assertEquals("p1", result.getPosition());
        Assertions.assertEquals(newDate.getTime() / 1000, result.getBorn().getTime() / 1000);
        Assertions.assertTrue(passwordUtils.encoder.matches("p1", result.getPassword()));
        Assertions.assertEquals(EmployeeRole.USER, result.getRole());

        var incorrectUpdate1 = new Employee(null, "forUpdate1", "p1", "fake", "p1", newDate, EmployeeRole.ADMIN);

        Assertions.assertThrows(
            ResponseStatusException.class,
            () -> staffService.updateEmployee("forUpdate1", incorrectUpdate1),
            "409 CONFLICT \"Employee with same login already exists\""
        );

        setSecurityContext("forUpdate1");

        var incorrectUpdate2 = new Employee(null, "forUpdate2", "p1", "forUpdate2", null, new Date(), null);
        var incorrectUpdate3 = new Employee(null, "forUpdate2", "p1", null, "p2", new Date(), null);
        var incorrectUpdate4 = new Employee(null, "forUpdate2", "p1", null, null, new Date(), EmployeeRole.ADMIN);

        Assertions.assertThrows(
            ResponseStatusException.class,
            () -> staffService.updateEmployee("forUpdate1", incorrectUpdate2),
            "403 FORBIDDEN \"Insufficiently rights\""
        );

        Assertions.assertThrows(
            ResponseStatusException.class,
            () -> staffService.updateEmployee("forUpdate1", incorrectUpdate3),
            "403 FORBIDDEN \"Insufficiently rights\""
        );

        Assertions.assertThrows(
            ResponseStatusException.class,
            () -> staffService.updateEmployee("forUpdate1", incorrectUpdate4),
            "403 FORBIDDEN \"Insufficiently rights\""
        );

        var correctUpdate2 = new Employee(null, "forUpdate2", "p2", null, null, new Date(), null);

        Assertions.assertNotNull(staffService.updateEmployee("forUpdate1", correctUpdate2));
    }

    private void setSecurityContext(String currentUser) {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn(currentUser);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }
}