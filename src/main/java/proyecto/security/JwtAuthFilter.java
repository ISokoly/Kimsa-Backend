// src/main/java/proyecto/security/JwtAuthFilter.java
package proyecto.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String COOKIE_NAME = "kimsa_jwt";
    private final JwtService jwt;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String token = extractFromCookie(request, COOKIE_NAME).orElse(null);
        if (token != null) {
            try {
                var claims = jwt.parser().parseSignedClaims(token).getPayload();
                String username = claims.getSubject();
                String role = String.valueOf(claims.get("role"));
                var auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception ignored) {
            }
        }

        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0");
        chain.doFilter(request, response);
    }

    private Optional<String> extractFromCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return Optional.empty();
        for (Cookie c : request.getCookies()) {
            if (name.equals(c.getName())) return Optional.ofNullable(c.getValue());
        }
        return Optional.empty();
    }
}
