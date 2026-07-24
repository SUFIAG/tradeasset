package com.tradesphere.ai.security.adapter.in.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response for SSO session details
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsoSessionResponse {
    
    private String sessionId;
    private String userId;
    private String email;
    private String displayName;
    private String providerId;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Map<String, String> attributes;
}

