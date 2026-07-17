package com.tradesphere.ai.security.adapter.out.persistence;

import com.tradesphere.ai.security.domain.model.SsoProtocol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * JPA Entity for Identity Provider
 */
@Entity
@Table(name = "identity_providers", indexes = {
    @Index(name = "idx_provider_id", columnList = "provider_id"),
    @Index(name = "idx_idp_tenant_id", columnList = "tenant_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityProviderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Column(name = "provider_name", nullable = false)
    private String providerName;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "protocol", nullable = false)
    private SsoProtocol protocol;

    @Column(name = "auth_endpoint", nullable = false, length = 500)
    private String authEndpoint;

    @Column(name = "token_endpoint", length = 500)
    private String tokenEndpoint;

    @Column(name = "user_info_endpoint", length = 500)
    private String userInfoEndpoint;

    @Column(name = "logout_endpoint", length = 500)
    private String logoutEndpoint;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "client_secret", nullable = false, length = 500)
    private String clientSecret;

    @ElementCollection
    @CollectionTable(name = "identity_provider_config", joinColumns = @JoinColumn(name = "provider_id"))
    @MapKeyColumn(name = "config_key")
    @Column(name = "config_value")
    @Builder.Default
    private Map<String, String> additionalConfig = new HashMap<>();

    @Column(name = "active")
    @Builder.Default
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

