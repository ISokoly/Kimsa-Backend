// src/main/java/proyecto/security/JwtService.java
package proyecto.security;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtService {
    private static final String SECRET = "KIMSA_ULTRA_SECRET_KEY_2025_a1B9xF7zQ2vR8sL3mN5pC4dT6jY0";
    private static final String ISSUER = "kimsa-api";
    private static final long EXP_MIN = 12 * 60; // 12 horas

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public String generate(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(EXP_MIN * 60);
        return Jwts.builder()
                .subject(subject)
                .issuer(ISSUER)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claims(claims)
                .signWith(key)
                .compact();
    }

    public JwtParser parser() {
        return Jwts.parser().verifyWith(key).build();
    }
}