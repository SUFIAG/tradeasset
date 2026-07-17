package com.tradesphere.ai.security.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * MFA Setup Domain Entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MfaSetup {
    
    private UUID id;
    private String userId;
    private String tenantId;
    
    private MfaMethod method;
    private String secret;
    
    @Builder.Default
    private List<String> backupCodes = new ArrayList<>();
    
    @Builder.Default
    private boolean enabled = false;
    
    @Builder.Default
    private boolean verified = false;
    
    private LocalDateTime createdAt;
    private LocalDateTime enabledAt;
    private LocalDateTime lastUsedAt;
    
    private String phoneNumber;  // for SMS
    private String email;        // for Email
    
    /**
     * Enable MFA after successful verification
     */
    public void enable() {
        this.enabled = true;
        this.verified = true;
        this.enabledAt = LocalDateTime.now();
    }
    
    /**
     * Disable MFA
     */
    public void disable() {
        this.enabled = false;
    }
    
    /**
     * Use backup code
     */
    public boolean useBackupCode(String code) {
        if (backupCodes.contains(code)) {
            backupCodes.remove(code);
            this.lastUsedAt = LocalDateTime.now();
            return true;
        }
        return false;
    }
    
    /**
     * Check if backup codes are available
     */
    public boolean hasBackupCodes() {
        return !backupCodes.isEmpty();
    }
}

