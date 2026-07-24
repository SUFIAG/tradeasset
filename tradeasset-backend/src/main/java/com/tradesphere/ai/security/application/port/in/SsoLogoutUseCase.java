package com.tradesphere.ai.security.application.port.in;

/**
 * Use Case: SSO Logout
 */
public interface SsoLogoutUseCase {
    
    /**
     * Logout from SSO session
     */
    void execute(String sessionId, String tenantId);
}

