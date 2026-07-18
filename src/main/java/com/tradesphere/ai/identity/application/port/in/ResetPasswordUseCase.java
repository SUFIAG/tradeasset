package com.tradesphere.ai.identity.application.port.in;

/**
 * Use Case: Reset Password
 * Resets user password using valid reset token
 */
public interface ResetPasswordUseCase {

    /**
     * Reset password using token
     * @param token Reset token from email
     * @param newPassword New password
     * @return Success message
     */
    String resetPassword(String token, String newPassword);
}

