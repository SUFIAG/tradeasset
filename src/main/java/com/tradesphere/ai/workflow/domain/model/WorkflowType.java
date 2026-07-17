package com.tradesphere.ai.workflow.domain.model;

/**
 * Workflow type enumeration
 * Defines different workflow templates
 */
public enum WorkflowType {
    
    /**
     * Customer approval workflow
     */
    CUSTOMER_APPROVAL,
    
    /**
     * LC application approval
     */
    LC_APPROVAL,
    
    /**
     * Document verification workflow
     */
    DOCUMENT_VERIFICATION,
    
    /**
     * LC amendment approval
     */
    LC_AMENDMENT,
    
    /**
     * Payment authorization
     */
    PAYMENT_AUTHORIZATION,
    
    /**
     * Compliance review
     */
    COMPLIANCE_REVIEW,
    
    /**
     * Risk assessment
     */
    RISK_ASSESSMENT,
    
    /**
     * Credit limit approval
     */
    CREDIT_LIMIT_APPROVAL;
    
    /**
     * Get default number of approval levels
     */
    public int getDefaultApprovalLevels() {
        return switch (this) {
            case CUSTOMER_APPROVAL, CREDIT_LIMIT_APPROVAL -> 3; // Junior, Senior, Manager
            case LC_APPROVAL, LC_AMENDMENT -> 4; // Ops, Credit, Compliance, Manager
            case PAYMENT_AUTHORIZATION -> 2; // Ops, Manager
            case DOCUMENT_VERIFICATION -> 1; // Ops
            case COMPLIANCE_REVIEW, RISK_ASSESSMENT -> 2; // Analyst, Manager
        };
    }
}

