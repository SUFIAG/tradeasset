package com.tradesphere.ai.security.application.port.in;

import java.util.List;

/**
 * Use Case: Regenerate MFA Backup Codes
 */
public interface RegenerateBackupCodesUseCase {

    /**
     * Regenerate backup codes for user
     */
    List<String> execute(String userId, String tenantId);
}

