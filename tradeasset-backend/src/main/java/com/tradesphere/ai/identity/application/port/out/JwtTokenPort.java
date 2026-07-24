package com.tradesphere.ai.identity.application.port.out;

import java.util.Set;
import java.util.UUID;

public interface JwtTokenPort {
    String generateAccessToken(UUID userId, String username, Set<String> roles, Set<String> permissions);
    String generateRefreshToken(UUID userId);
    boolean validateToken(String token);
    UUID getUserIdFromToken(String token);
    String getUsernameFromToken(String token);
}

