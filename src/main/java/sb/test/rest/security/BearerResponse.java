package sb.test.rest.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BearerResponse {
    private final String token;
}
