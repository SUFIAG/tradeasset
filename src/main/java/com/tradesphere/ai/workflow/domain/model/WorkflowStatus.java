package com.tradesphere.ai.workflow.domain.model;

/**
 * Workflow status enumeration
 * Lifecycle of workflow instances
 */
public enum WorkflowStatus {
    
    /**
     * Workflow created but not started
     */
    PENDING,
    
    /**
     * Workflow is actively running
     */
    IN_PROGRESS,
    
    /**
     * Waiting for manual task completion
     */
    WAITING_FOR_INPUT,
    
    /**
     * Workflow paused manually
     */
    PAUSED,
    
    /**
     * Workflow completed successfully
     */
    COMPLETED,
    
    /**
     * Workflow rejected/declined
     */
    REJECTED,
    
    /**
     * Workflow cancelled
     */
    CANCELLED,
    
    /**
     * Workflow failed with error
     */
    FAILED,
    
    /**
     * Workflow expired (SLA breach)
     */
    EXPIRED;
    
    public boolean isActive() {
        return this == IN_PROGRESS || this == WAITING_FOR_INPUT;
    }
    
    public boolean isTerminal() {
        return this == COMPLETED || this == REJECTED || this == CANCELLED || 
               this == FAILED || this == EXPIRED;
    }
}

