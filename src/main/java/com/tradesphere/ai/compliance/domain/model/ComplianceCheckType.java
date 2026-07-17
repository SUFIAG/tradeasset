package com.tradesphere.ai.compliance.domain.model;

/**
 * Compliance Check Type Enumeration
 * Defines the type of compliance screening being performed
 */
public enum ComplianceCheckType {
    /**
     * Customer onboarding compliance check
     */
    CUSTOMER_ONBOARDING,

    /**
     * Ongoing periodic customer screening
     */
    PERIODIC_SCREENING,

    /**
     * Transaction-based compliance check
     */
    TRANSACTION_SCREENING,

    /**
     * Document-based compliance check
     */
    DOCUMENT_SCREENING,

    /**
     * Enhanced due diligence check
     */
    ENHANCED_DUE_DILIGENCE,

    /**
     * Re-screening after system update
     */
    RESCREENING
}

