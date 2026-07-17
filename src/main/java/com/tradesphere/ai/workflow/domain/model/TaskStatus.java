package com.tradesphere.ai.workflow.domain.model;

/**
 * Task status enumeration
 */
public enum TaskStatus {
    
    /**
     * Task created but not assigned
     */
    CREATED,
    
    /**
     * Task assigned to user
     */
    ASSIGNED,
    
    /**
     * Task in progress
     */
    IN_PROGRESS,
    
    /**
     * Task completed successfully
     */
    COMPLETED,
    
    /**
     * Task rejected
     */
    REJECTED,
    
    /**
     * Task cancelled
     */
    CANCELLED,
    
    /**
     * Task escalated due to SLA breach
     */
    ESCALATED;
    
    public boolean isActive() {
        return this == ASSIGNED || this == IN_PROGRESS || this == ESCALATED;
    }
    
    public boolean isTerminal() {
        return this == COMPLETED || this == REJECTED || this == CANCELLED;
    }
}

