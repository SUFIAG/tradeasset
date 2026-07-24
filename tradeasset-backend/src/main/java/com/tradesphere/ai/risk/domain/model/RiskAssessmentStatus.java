package com.tradesphere.ai.risk.domain.model;

/**
 * Risk Assessment Status Enumeration
 */
public enum RiskAssessmentStatus {
    /**
     * Assessment pending
     */
    PENDING,
    
    /**
     * Assessment in progress
     */
    IN_PROGRESS,
    
    /**
     * Assessment completed
     */
    COMPLETED,
    
    /**
     * Assessment failed
     */
    FAILED,
    
    /**
     * Assessment requires review
     */
    REQUIRES_REVIEW
}

