package com.tradesphere.ai.lc.domain.model;

/**
 * Letter of Credit lifecycle status
 */
public enum LCStatus {
    
    /**
     * LC is being drafted
     */
    DRAFT,
    
    /**
     * LC submitted for approval
     */
    SUBMITTED,
    
    /**
     * LC pending approval
     */
    PENDING_APPROVAL,

    /**
     * LC is under review
     */
    UNDER_REVIEW,
    
    /**
     * LC pending credit limit check
     */
    PENDING_CREDIT_CHECK,
    
    /**
     * LC pending compliance check
     */
    PENDING_COMPLIANCE,
    
    /**
     * LC approved and ready for issuance
     */
    APPROVED,
    
    /**
     * LC rejected
     */
    REJECTED,
    
    /**
     * LC issued to beneficiary
     */
    ISSUED,
    
    /**
     * LC is active and in use
     */
    ACTIVE,

    /**
     * Documents submitted by beneficiary
     */
    DOCUMENTS_SUBMITTED,

    /**
     * Documents received and under review
     */
    DOCUMENTS_RECEIVED,
    
    /**
     * Documents accepted (compliant)
     */
    DOCUMENTS_ACCEPTED,
    
    /**
     * Documents rejected (discrepancies found)
     */
    DOCUMENTS_REJECTED,
    
    /**
     * Discrepancies waived, proceeding to payment
     */
    DISCREPANCIES_WAIVED,
    
    /**
     * Payment authorized
     */
    PAYMENT_AUTHORIZED,
    
    /**
     * Payment executed
     */
    PAID,
    
    /**
     * LC completed successfully
     */
    COMPLETED,
    
    /**
     * LC expired without utilization
     */
    EXPIRED,
    
    /**
     * LC cancelled
     */
    CANCELLED,
    
    /**
     * LC amendment requested
     */
    AMENDMENT_REQUESTED,
    
    /**
     * LC amended
     */
    AMENDED
}

