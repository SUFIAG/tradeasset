package com.tradesphere.ai.security.application.port.in;

import com.tradesphere.ai.security.application.port.in.dto.DisableMfaCommand;

/**
 * Use Case: Disable MFA
 */
public interface DisableMfaUseCase {
    
    /**
     * Disable MFA for user
     */
    void execute(DisableMfaCommand command);
}

