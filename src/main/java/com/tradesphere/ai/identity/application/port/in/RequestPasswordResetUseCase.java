package com.tradesphere.ai.identity.application.port.in;

/**
 * Use Case: Request Password Reset
 * Initiates password reset process by sending reset token via email
 */
public interface RequestPasswordResetUseCase {

    /**
     * Request password reset for user
     * @param email User's email address
     * @return Reset token (in production, send via email only)
     */
    String requestPasswordReset(String email);
}

