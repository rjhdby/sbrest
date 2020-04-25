package sb.test.rest.service;

import org.springframework.stereotype.Service;
import sb.test.rest.domain.Employee;

@Service
public class EmployeeValidator {
    public boolean isAdmin(Employee employee) {
        if (employee.getRole() == null) {
            return false;
        }
        return employee.getRole().isAdmin();
    }

    public boolean isValidNewEmployee(Employee employee) {
        return employee.getName() != null
            && employee.getPosition() != null
            && employee.getLogin() != null
            && employee.getPassword() != null
            && employee.getBorn() != null
            && employee.getRole() != null
            && employee.getId() == null;
    }

    public boolean isValidSelfUpdatedEmployee(Employee employee) {
        return employee.getLogin() == null
            && employee.getPassword() == null
            && employee.getRole() == null;
    }
}
