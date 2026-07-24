package com.tradesphere.ai.security.application.port.in;

import com.tradesphere.ai.security.application.port.in.dto.VerifyMfaCommand;

/**
 * Use Case: Verify MFA Code
 */
public interface VerifyMfaCodeUseCase {
    
    /**
     * Verify MFA code (TOTP, OTP, or backup code)
     */
    boolean execute(VerifyMfaCommand command);
}

