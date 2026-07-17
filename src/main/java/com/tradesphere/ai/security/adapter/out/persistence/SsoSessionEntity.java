package com.tradesphere.ai.security.adapter.out.persistence;

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
 * JPA Entity for SSO Session
 */
@Entity
@Table(name = "sso_sessions", indexes = {
    @Index(name = "idx_session_id", columnList = "session_id"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_tenant_id", columnList = "tenant_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsoSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "session_id", nullable = false, unique = true)
    private String sessionId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "email")
    private String email;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @ElementCollection
    @CollectionTable(name = "sso_session_attributes", joinColumns = @JoinColumn(name = "session_id"))
    @MapKeyColumn(name = "attribute_key")
    @Column(name = "attribute_value")
    @Builder.Default
    private Map<String, String> attributes = new HashMap<>();

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "active")
    @Builder.Default
    private boolean active = true;
}

