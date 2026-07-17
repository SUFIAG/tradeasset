package com.tradesphere.ai.security.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response from SSO authentication
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsoAuthenticationResponse {
    
    private String sessionId;
    private String userId;
    private String email;
    private String displayName;
    private String tenantId;
    private boolean success;
    private LocalDateTime expiresAt;
    private Map<String, String> attributes;
}

