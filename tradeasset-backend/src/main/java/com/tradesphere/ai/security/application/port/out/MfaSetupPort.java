package com.tradesphere.ai.security.application.port.out;

import com.tradesphere.ai.security.domain.model.MfaSetup;

import java.util.Optional;

/**
 * Port for MFA Setup persistence
 */
public interface MfaSetupPort {
    
    /**
     * Save MFA setup
     */
    MfaSetup save(MfaSetup setup);
    
    /**
     * Find MFA setup by user ID
     */
    Optional<MfaSetup> findByUserId(String userId, String tenantId);
    
    /**
     * Update MFA setup
     */
    MfaSetup update(MfaSetup setup);
    
    /**
     * Delete MFA setup
     */
    void delete(String userId, String tenantId);
    
    /**
     * Check if MFA is enabled for user
     */
    boolean isMfaEnabled(String userId, String tenantId);
}

