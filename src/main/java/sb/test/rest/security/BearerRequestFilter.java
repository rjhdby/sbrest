package sb.test.rest.security;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
@AllArgsConstructor
public class BearerRequestFilter extends OncePerRequestFilter {
    private final UserDetailsService bearerUserDetailsService;
    private final BearerTokenUtil bearerTokenUtil;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain
    ) throws ServletException, IOException {
        final var authorizationHeader = request.getHeader("Authorization");

        if (!hasBearerToken(authorizationHeader)) {
            chain.doFilter(request, response);
            return;
        }

        var bearer = getToken(authorizationHeader);
        var userName = getUserName(bearer);
        if (mustBeAuthenticated(userName)) {
            UserDetails userDetails = bearerUserDetailsService.loadUserByUsername(userName);
            if (bearerTokenUtil.validateToken(bearer, userDetails)) {
                authenticate(userDetails, request);
            }
        }

        chain.doFilter(request, response);
    }

    @Nullable
    private String getUserName(@NonNull String bearer) {
        try {
            return bearerTokenUtil.getLoginFromToken(bearer);
        } catch (IllegalArgumentException | ExpiredJwtException e) {
            return null;
        }
    }

    private boolean hasBearerToken(@Nullable String authString) {
        return authString != null && authString.startsWith("Bearer ");
    }

    @NonNull
    private String getToken(@NonNull String bearer) {
        return bearer.substring(7);
    }

    private boolean mustBeAuthenticated(@Nullable String userName) {
        return userName != null && SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private void authenticate(UserDetails userDetails, HttpServletRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}