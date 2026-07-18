package com.tradesphere.ai.bankguarantee.domain.model;

/**
 * Bank Guarantee Status Lifecycle
 */
public enum BankGuaranteeStatus {

    DRAFT,              // Initial state
    PENDING_APPROVAL,   // Submitted for approval
    APPROVED,           // Approved but not yet issued
    REJECTED,           // Rejected during approval
    ACTIVE,             // Issued and active
    CLAIMED,            // Claim has been made
    SETTLED,            // Claim settled/paid
    EXPIRED,            // Validity period ended
    CANCELLED,          // Cancelled before expiry
    INVOKED,            // Claim paid out
    RELEASED;           // Released by beneficiary

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isTerminal() {
        return this == EXPIRED || this == CANCELLED ||
               this == INVOKED || this == RELEASED;
    }

    public boolean canBeClaimed() {
        return this == ACTIVE;
    }
}

