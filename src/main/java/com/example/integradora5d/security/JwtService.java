package com.example.integradora5d.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private static final String ROLES_CLAIM = "roles";
    private final String secret;
    private final long expirationMs;
    private volatile SecretKey signingKey;

    public JwtService(

            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.secret = secret;
        this.expirationMs = expirationMs;
    }
    private SecretKey signingKey() {
        if (signingKey == null) {
            synchronized (this) {
                if (signingKey == null) {
                    byte[] bytes =
                            secret.getBytes(StandardCharsets.UTF_8);

                    if (bytes.length < 32) {
                        throw new IllegalStateException(
                                "jwt.secret debe medir al menos 32 bytes en UTF-8 para HS256 (JJWT)");

                    }
                    signingKey = Keys.hmacShaKeyFor(bytes);
                }
            }
        }
        return signingKey;
    }

    public String generateAccessToken(String email, Collection<String>
            roleAuthorities) {

        Instant now = Instant.now();
        Instant exp = now.plusMillis(expirationMs);
        return Jwts.builder()

                .subject(email)
                .claim(ROLES_CLAIM, List.copyOf(roleAuthorities))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(signingKey())
                .compact();

    }
    public Claims parseClaims(String token) {
        return Jwts.parser()

                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public List<String> extractRoleAuthorities(String token) {
        List<?> raw = parseClaims(token).get(ROLES_CLAIM, List.class);
        if (raw == null) {
            return List.of();
        }
        return

                raw.stream().map(Object::toString).collect(Collectors.toUnmodifiableList());
    }
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    public boolean isTokenValidForUser(String token, String expectedEmail) {
        if (!isTokenValid(token)) {
            return false;
        }
        String subject = extractEmail(token);
        return subject != null && subject.equals(expectedEmail);
    }
}