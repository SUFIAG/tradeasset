package com.tradesphere.ai.security.application.port.in;

import com.tradesphere.ai.security.application.port.in.dto.GenerateOtpCommand;

/**
 * Use Case: Generate MFA OTP
 */
public interface GenerateMfaOtpUseCase {
    
    /**
     * Generate OTP for SMS/Email delivery
     */
    String execute(GenerateOtpCommand command);
}

