package com.tradesphere.ai.identity.application.port.in;

import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class AuthenticationResult {
    String userId;
    String username;
    String email;
    String accessToken;
    String refreshToken;
    long expiresIn;
    Set<String> roles;
    Set<String> permissions;
}

