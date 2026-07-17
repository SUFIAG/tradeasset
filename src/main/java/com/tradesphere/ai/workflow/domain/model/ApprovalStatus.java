package com.tradesphere.ai.workflow.domain.model;

/**
 * Approval status for tasks/workflows
 * Business-friendly status for approval decisions
 */
public enum ApprovalStatus {
    
    /**
     * Pending approval
     */
    PENDING,
    
    /**
     * Approved by approver
     */
    APPROVED,
    
    /**
     * Rejected by approver
     */
    REJECTED,
    
    /**
     * Returned for clarification/correction
     */
    RETURNED,
    
    /**
     * Conditionally approved (with conditions)
     */
    CONDITIONAL;
    
    /**
     * Check if status is final (no further action)
     */
    public boolean isFinal() {
        return this == APPROVED || this == REJECTED;
    }
    
    /**
     * Check if status requires action
     */
    public boolean requiresAction() {
        return this == PENDING || this == RETURNED || this == CONDITIONAL;
    }
}


