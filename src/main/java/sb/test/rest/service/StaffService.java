package sb.test.rest.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sb.test.rest.domain.Employee;
import sb.test.rest.repository.UsersRepositoryDAO;

@Service
@AllArgsConstructor
public class StaffService {
    private final UsersRepositoryDAO repository;
    private final EmployeeValidator validator;
    private final PasswordUtils passwordUtils;

    public Page<Employee> list(PageRequest pageRequest) {
        return repository.findAll(pageRequest);
    }

    public Page<Employee> findByName(@NonNull String name, PageRequest pageRequest) {
        return repository.findAllByNameLike(String.format("%%%s%%", name), pageRequest);
    }

    public Employee getEmployee(@NonNull String login) {
        var employee = repository.findByLogin(login);
        if (employee == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found");
        }
        return employee;
    }

    @SneakyThrows
    public Employee addEmployee(@NonNull Employee originalEmployee) {
        var employee = (Employee) originalEmployee.clone();

        if (!getCurrentEmployee().getRole().isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficiently rights");
        }

        if (!validator.isValidNewEmployee(employee)) {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "Invalid payload");
        }

        if(repository.findByLogin(employee.getLogin()) != null){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee with same login already exists");
        }

        employee.setPassword(passwordUtils.encode(employee.getPassword()));

        return repository.save(employee);
    }

    public void deleteEmployee(String login) {
        if (!validator.isAdmin(getCurrentEmployee())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficiently rights");
        }

        repository.delete(getEmployee(login));
    }

    @SneakyThrows
    public Employee updateEmployee(String login, Employee updatedEmployee) {
        var employee = (Employee) updatedEmployee.clone();

        var originalEmployee = getEmployee(login);

        if(repository.findByLogin(employee.getLogin()) != null){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee with same login already exists");
        }

        if (validator.isAdmin(getCurrentEmployee()) || validator.isValidSelfUpdatedEmployee(employee)) {
            prepareEmployeeForUpdate(originalEmployee, employee);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficiently rights");
        }

        return repository.save(originalEmployee);
    }

    private void prepareEmployeeForUpdate(Employee oldEmployee, Employee newEmployee) {
        if (newEmployee.getRole() != null) {
            oldEmployee.setRole(newEmployee.getRole());
        }

        if (newEmployee.getBorn() != null) {
            oldEmployee.setBorn(newEmployee.getBorn());
        }

        if (newEmployee.getPosition() != null) {
            oldEmployee.setPosition(newEmployee.getPosition());
        }

        if (newEmployee.getLogin() != null) {
            oldEmployee.setLogin(newEmployee.getLogin());
        }

        if (newEmployee.getName() != null) {
            oldEmployee.setName(newEmployee.getName());
        }

        if (newEmployee.getPassword() != null) {
            oldEmployee.setPassword(passwordUtils.encode(newEmployee.getPassword()));
        }
    }

    private Employee getCurrentEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        return repository.findByLogin(currentPrincipalName);
    }
}
