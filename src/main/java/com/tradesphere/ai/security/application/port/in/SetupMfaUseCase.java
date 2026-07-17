package com.tradesphere.ai.security.application.port.in;

import com.tradesphere.ai.security.application.port.in.dto.MfaSetupCommand;
import com.tradesphere.ai.security.application.port.in.dto.MfaSetupResponse;

/**
 * Use Case: Setup MFA
 */
public interface SetupMfaUseCase {
    
    /**
     * Setup MFA for user
     */
    MfaSetupResponse execute(MfaSetupCommand command);
}

