package com.tradesphere.ai.customer.domain.model;

/**
 * Compliance Status Enumeration
 * Represents the compliance screening status for a customer
 */
public enum ComplianceStatus {
    /**
     * Compliance check not yet performed
     */
    PENDING,

    /**
     * Compliance check in progress
     */
    IN_PROGRESS,

    /**
     * Customer cleared all compliance checks
     */
    CLEARED,

    /**
     * Customer flagged for review (potential issues found)
     */
    FLAGGED,

    /**
     * Customer rejected due to compliance violations
     */
    REJECTED;

    public boolean isCleared() {
        return this == CLEARED;
    }

    public boolean requiresReview() {
        return this == FLAGGED;
    }

    public boolean isRejected() {
        return this == REJECTED;
    }
}

