package com.tradesphere.ai.identity.adapter.out.security;

import com.tradesphere.ai.identity.application.port.out.JwtTokenPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * JWT Token Service
 * Implements JWT token generation and validation
 */
@Component
@Slf4j
public class JwtTokenService implements JwtTokenPort {

    private final SecretKey secretKey;
    private final long expirationMs;
    private final long refreshExpirationMs;

    public JwtTokenService(
            @Value("${tradesphere.security.jwt.secret}") String secret,
            @Value("${tradesphere.security.jwt.expiration:86400000}") long expirationMs,
            @Value("${tradesphere.security.jwt.refresh-expiration:604800000}") long refreshExpirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    @Override
    public String generateAccessToken(UUID userId, String username, Set<String> roles, Set<String> permissions) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        claims.put("username", username);
        claims.put("roles", roles);
        claims.put("permissions", permissions);
        claims.put("type", "ACCESS");

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    @Override
    public String generateRefreshToken(UUID userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        claims.put("type", "REFRESH");

        return Jwts.builder()
                .claims(claims)
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .signWith(secretKey)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public UUID getUserIdFromToken(String token) {
        Claims claims = extractClaims(token);
        return UUID.fromString(claims.get("userId", String.class));
    }

    @Override
    public String getUsernameFromToken(String token) {
        Claims claims = extractClaims(token);
        return claims.getSubject();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

