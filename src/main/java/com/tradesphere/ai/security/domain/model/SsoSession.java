package com.tradesphere.ai.security.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * SSO Session Domain Entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsoSession {

    private UUID id;
    private String sessionId;
    private String userId;
    private String email;
    private String displayName;
    private String providerId;
    private String tenantId;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime lastAccessedAt;

    @Builder.Default
    private Map<String, String> attributes = new HashMap<>();

    private String ipAddress;
    private String userAgent;

    @Builder.Default
    private boolean active = true;

    /**
     * Check if session is expired
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Check if session is valid
     */
    public boolean isValid() {
        return active && !isExpired();
    }

    /**
     * Refresh session expiry
     */
    public void refresh(int timeoutSeconds) {
        this.expiresAt = LocalDateTime.now().plusSeconds(timeoutSeconds);
        this.lastAccessedAt = LocalDateTime.now();
    }

    /**
     * Invalidate session
     */
    public void invalidate() {
        this.active = false;
    }
}

