package com.tradesphere.ai.security.application.port.in;

import com.tradesphere.ai.security.domain.model.SsoSession;

/**
 * Use Case: Validate SSO Session
 */
public interface ValidateSsoSessionUseCase {
    
    /**
     * Validate SSO session
     */
    boolean validate(String sessionId, String tenantId);
    
    /**
     * Get SSO session
     */
    SsoSession getSession(String sessionId, String tenantId);
    
    /**
     * Refresh SSO session
     */
    void refresh(String sessionId, String tenantId);
}

