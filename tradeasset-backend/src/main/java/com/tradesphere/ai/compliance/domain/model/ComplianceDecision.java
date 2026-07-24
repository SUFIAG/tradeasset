package com.tradesphere.ai.compliance.domain.model;

/**
 * Compliance Decision Enumeration
 * Final decision after compliance screening
 */
public enum ComplianceDecision {
    /**
     * Manually approved by compliance officer
     */
    APPROVED,

    /**
     * Automatically approved by AI (no hits)
     */
    AUTO_APPROVED,

    /**
     * Manually rejected by compliance officer
     */
    REJECTED,

    /**
     * Automatically rejected by AI (high confidence hit)
     */
    AUTO_REJECTED,

    /**
     * Requires manual review by compliance officer
     */
    MANUAL_REVIEW_REQUIRED,

    /**
     * Cleared as false positive
     */
    FALSE_POSITIVE
}

