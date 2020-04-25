package sb.test.rest.rest;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sb.test.rest.domain.Employee;
import sb.test.rest.rest.pagination.PagedListDTO;
import sb.test.rest.security.BearerResponse;
import sb.test.rest.security.BearerTokenUtil;
import sb.test.rest.security.BearerUserDetailsService;
import sb.test.rest.service.StaffService;

@RestController("/api")
@CrossOrigin
@AllArgsConstructor
public class Api {
    private final AuthenticationManager authenticationManager;
    private final BearerTokenUtil bearerTokenUtil;
    private final BearerUserDetailsService userDetailsService;
    private final StaffService staffService;

    private final static Sort sorter = Sort.by("name").ascending();

    @PostMapping("/auth")
    public BearerResponse createAuthenticationToken(
        @RequestParam @NonNull String login,
        @RequestParam @NonNull String password
    ) {
        authenticate(login, password);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(login);
        final String token = bearerTokenUtil.generateToken(userDetails);
        return new BearerResponse(token);
    }

    @GetMapping("/list")
    public PagedListDTO<Employee> list(
        @RequestHeader("Authorization") @NonNull String token,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size
    ) {
        return new PagedListDTO<>(staffService.list(getPagination(page, size)));
    }

    @GetMapping("/find")
    public PagedListDTO<Employee> find(
        @RequestHeader("Authorization") @NonNull String token,
        @RequestParam String name,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size
    ) {
        return new PagedListDTO<>(staffService.findByName(name, getPagination(page, size)));
    }

    @PostMapping("/add")
    public Employee add(
        @RequestHeader("Authorization") @NonNull String token,
        @RequestBody Employee employee
    ) {
        return staffService.addEmployee(employee);
    }

    @PatchMapping("/update/{login}")
    public Employee update(
        @RequestHeader("Authorization") @NonNull String token,
        @PathVariable String login,
        @RequestBody Employee employee
    ) {
        return staffService.updateEmployee(login, employee);
    }

    @DeleteMapping("/remove/{login}")
    public void add(
        @RequestHeader("Authorization") @NonNull String token,
        @PathVariable String login
    ) {
        staffService.deleteEmployee(login);
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<Exception> handleAllExceptions(RuntimeException ex) {
        return new ResponseEntity<>(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private PageRequest getPagination(Integer page, Integer size) {
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 10;
        }

        return PageRequest.of(page, size, sorter);
    }

    private void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User disabled");
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }
}
