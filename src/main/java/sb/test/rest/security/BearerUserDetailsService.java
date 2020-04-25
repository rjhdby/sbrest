package sb.test.rest.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sb.test.rest.service.StaffService;

import java.util.ArrayList;

@Service
@AllArgsConstructor
public class BearerUserDetailsService implements UserDetailsService {
    private final StaffService staffService;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        var employee = staffService.getEmployee(login);
        return new User(login, employee.getPassword(), new ArrayList<>());
    }
}
