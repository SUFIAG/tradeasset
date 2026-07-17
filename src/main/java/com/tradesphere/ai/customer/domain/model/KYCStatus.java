package com.tradesphere.ai.customer.domain.model;

/**
 * KYC (Know Your Customer) Status Enumeration
 * Tracks the KYC verification lifecycle
 */
public enum KYCStatus {
    /**
     * KYC process not started
     */
    PENDING,

    /**
     * KYC documentation being collected/reviewed
     */
    IN_PROGRESS,

    /**
     * All KYC documents collected and verified
     */
    COMPLETED,

    /**
     * KYC verification rejected due to issues
     */
    REJECTED,

    /**
     * KYC certification has expired, needs renewal
     */
    EXPIRED;

    public boolean isValid() {
        return this == COMPLETED;
    }

    public boolean needsAction() {
        return this == PENDING || this == IN_PROGRESS || this == EXPIRED;
    }

    public boolean canBeCompleted() {
        return this == IN_PROGRESS;
    }
}

