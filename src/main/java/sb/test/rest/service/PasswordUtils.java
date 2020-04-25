package sb.test.rest.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordUtils {
    final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String encode(String password){
        return encoder.encode(password);
    }

    public PasswordEncoder getEncoder(){
        return encoder;
    }
}
