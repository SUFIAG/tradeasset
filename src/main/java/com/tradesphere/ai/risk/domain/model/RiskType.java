package com.tradesphere.ai.risk.domain.model;

/**
 * Risk Type Enumeration
 * Categories of risks assessed by the system
 */
public enum RiskType {
    /**
     * Credit risk - Risk of customer default
     */
    CREDIT_RISK,
    
    /**
     * Country risk - Political and economic risks
     */
    COUNTRY_RISK,
    
    /**
     * Market risk - Currency, commodity price risks
     */
    MARKET_RISK,
    
    /**
     * Operational risk - Process, system, human risks
     */
    OPERATIONAL_RISK,
    
    /**
     * Compliance risk - Regulatory, legal risks
     */
    COMPLIANCE_RISK,
    
    /**
     * Transaction risk - Specific transaction risks
     */
    TRANSACTION_RISK,
    
    /**
     * Documentary risk - Document authenticity and validity risks
     */
    DOCUMENTARY_RISK
}

