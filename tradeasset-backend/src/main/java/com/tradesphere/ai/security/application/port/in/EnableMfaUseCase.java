package com.tradesphere.ai.security.application.port.in;

import com.tradesphere.ai.security.application.port.in.dto.EnableMfaCommand;

/**
 * Use Case: Enable MFA
 */
public interface EnableMfaUseCase {
    
    /**
     * Enable MFA after verification
     */
    void execute(EnableMfaCommand command);
}

