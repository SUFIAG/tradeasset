package com.tradesphere.ai.scf.domain.model;

public enum SCFStatus {
    PENDING,      // Awaiting approval
    APPROVED,     // Approved for financing
    FUNDED,       // Funds disbursed
    REPAID,       // Fully repaid
    DEFAULTED,    // Payment default
    CANCELLED;    // Cancelled before funding

    public boolean isActive() {
        return this == FUNDED;
    }

    public boolean isTerminal() {
        return this == REPAID || this == DEFAULTED || this == CANCELLED;
    }
}

