package com.tradesphere.ai.security.application.port.out;

import com.tradesphere.ai.security.domain.model.SsoSession;

import java.util.Optional;

/**
 * Port for SSO Session persistence
 */
public interface SsoSessionPort {

    /**
     * Save SSO session
     */
    SsoSession save(SsoSession session);

    /**
     * Find session by ID
     */
    Optional<SsoSession> findBySessionId(String sessionId, String tenantId);

    /**
     * Find session by user ID
     */
    Optional<SsoSession> findByUserId(String userId, String tenantId);

    /**
     * Delete session
     */
    void delete(String sessionId, String tenantId);

    /**
     * Delete all sessions for user
     */
    void deleteByUserId(String userId, String tenantId);

    /**
     * Update session
     */
    SsoSession update(SsoSession session);
}

