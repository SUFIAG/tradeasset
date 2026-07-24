package com.tradesphere.ai.bankguarantee.domain.model;

/**
 * Types of Bank Guarantees
 */
public enum BankGuaranteeType {
    
    /**
     * Bid Bond / Tender Guarantee
     * Protects beneficiary if bidder withdraws bid or fails to sign contract
     */
    BID_BOND,
    
    /**
     * Performance Guarantee
     * Protects beneficiary against non-performance of contractual obligations
     */
    PERFORMANCE,
    
    /**
     * Advance Payment Guarantee
     * Protects beneficiary against non-repayment of advance payment
     */
    ADVANCE_PAYMENT,
    
    /**
     * Retention Money Guarantee
     * Replaces retention money withheld by beneficiary
     */
    RETENTION,
    
    /**
     * Payment Guarantee
     * Guarantees payment obligations
     */
    PAYMENT,
    
    /**
     * Financial Guarantee
     * General financial obligations guarantee
     */
    FINANCIAL;
    
    public String getDescription() {
        return switch (this) {
            case BID_BOND -> "Bid Bond/Tender Guarantee";
            case PERFORMANCE -> "Performance Guarantee";
            case ADVANCE_PAYMENT -> "Advance Payment Guarantee";
            case RETENTION -> "Retention Money Guarantee";
            case PAYMENT -> "Payment Guarantee";
            case FINANCIAL -> "Financial Guarantee";
        };
    }
    
    public int getTypicalValidityMonths() {
        return switch (this) {
            case BID_BOND -> 3;
            case PERFORMANCE -> 12;
            case ADVANCE_PAYMENT -> 6;
            case RETENTION -> 24;
            case PAYMENT -> 12;
            case FINANCIAL -> 12;
        };
    }
}

