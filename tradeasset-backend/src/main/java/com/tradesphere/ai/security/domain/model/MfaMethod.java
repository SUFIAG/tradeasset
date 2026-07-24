package com.tradesphere.ai.security.domain.model;

/**
 * Multi-Factor Authentication Methods
 */
public enum MfaMethod {
    /**
     * Time-based One-Time Password (Google Authenticator, Authy, etc.)
     */
    TOTP,
    
    /**
     * SMS-based OTP
     */
    SMS,
    
    /**
     * Email-based OTP
     */
    EMAIL,
    
    /**
     * Backup recovery codes
     */
    BACKUP_CODE
}

