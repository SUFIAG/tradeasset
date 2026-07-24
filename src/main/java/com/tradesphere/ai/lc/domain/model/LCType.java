package com.tradesphere.ai.lc.domain.model;

/**
 * Types of Letters of Credit
 */
public enum LCType {
    
    /**
     * Sight LC - Payment at sight (immediately upon presentation of documents)
     */
    SIGHT,
    
    /**
     * Usance/Deferred LC - Payment at a future date
     */
    USANCE,
    
    /**
     * Deferred Payment LC - Payment deferred to a specific date
     */
    DEFERRED_PAYMENT,

    /**
     * Acceptance LC - Bank accepts a time draft
     */
    ACCEPTANCE,

    /**
     * Negotiation LC - Nominated bank negotiates documents
     */
    NEGOTIATION,

    /**
     * Confirmed LC - Additional guarantee from another bank
     */
    CONFIRMED,
    
    /**
     * Unconfirmed LC - No additional guarantee
     */
    UNCONFIRMED,
    
    /**
     * Revolving LC - Can be used multiple times
     */
    REVOLVING,
    
    /**
     * Transferable LC - Can be transferred to another beneficiary
     */
    TRANSFERABLE,
    
    /**
     * Back-to-Back LC - Used to issue another LC
     */
    BACK_TO_BACK,
    
    /**
     * Red Clause LC - Allows advance payment
     */
    RED_CLAUSE,
    
    /**
     * Green Clause LC - Allows payment for warehousing
     */
    GREEN_CLAUSE,
    
    /**
     * Standby LC - Acts as a guarantee
     */
    STANDBY
}

