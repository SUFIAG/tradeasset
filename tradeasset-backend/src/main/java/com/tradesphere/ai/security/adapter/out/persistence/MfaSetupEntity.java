package com.tradesphere.ai.security.adapter.out.persistence;

import com.tradesphere.ai.security.domain.model.MfaMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA Entity for MFA Setup
 */
@Entity
@Table(name = "mfa_setups", indexes = {
    @Index(name = "idx_mfa_user_id", columnList = "user_id"),
    @Index(name = "idx_mfa_tenant_id", columnList = "tenant_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MfaSetupEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false)
    private MfaMethod method;
    
    @Column(name = "secret", nullable = false, length = 500)
    private String secret;
    
    @ElementCollection
    @CollectionTable(name = "mfa_backup_codes", joinColumns = @JoinColumn(name = "mfa_setup_id"))
    @Column(name = "backup_code")
    @Builder.Default
    private List<String> backupCodes = new ArrayList<>();
    
    @Column(name = "enabled")
    @Builder.Default
    private boolean enabled = false;
    
    @Column(name = "verified")
    @Builder.Default
    private boolean verified = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "enabled_at")
    private LocalDateTime enabledAt;
    
    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "email")
    private String email;
}

