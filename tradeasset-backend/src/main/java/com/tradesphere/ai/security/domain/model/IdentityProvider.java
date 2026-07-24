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
 * Identity Provider Configuration Domain Entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityProvider {

    private UUID id;
    private String providerId;
    private String providerName;
    private String tenantId;

    private SsoProtocol protocol;
    private String authEndpoint;
    private String tokenEndpoint;
    private String userInfoEndpoint;
    private String logoutEndpoint;

    private String clientId;
    private String clientSecret;

    @Builder.Default
    private Map<String, String> additionalConfig = new HashMap<>();

    @Builder.Default
    private boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Get configuration value
     */
    public String getConfig(String key) {
        return additionalConfig.get(key);
    }

    /**
     * Set configuration value
     */
    public void setConfig(String key, String value) {
        this.additionalConfig.put(key, value);
    }
}

