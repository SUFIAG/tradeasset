package com.tradesphere.ai.compliance.domain.model;

/**
 * Compliance Check Status Enumeration
 * Tracks the lifecycle status of a compliance check
 */
public enum ComplianceCheckStatus {
    /**
     * Check initiated but not yet started
     */
    PENDING,

    /**
     * Check is currently in progress
     */
    IN_PROGRESS,

    /**
     * Check completed successfully
     */
    COMPLETED,

    /**
     * Check failed due to technical error
     */
    FAILED,

    /**
     * Check cancelled
     */
    CANCELLED
}

