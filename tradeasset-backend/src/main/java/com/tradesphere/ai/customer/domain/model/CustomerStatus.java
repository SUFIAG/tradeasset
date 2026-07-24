package com.tradesphere.ai.customer.domain.model;

/**
 * Customer Status Enumeration
 * Represents the lifecycle status of a customer
 */
public enum CustomerStatus {
    /**
     * Customer record created but not yet submitted for approval
     */
    DRAFT,

    /**
     * Customer submitted for approval, awaiting review
     */
    PENDING_APPROVAL,

    /**
     * Customer approved but not yet activated
     */
    APPROVED,

    /**
     * Customer is active and can perform transactions
     */
    ACTIVE,

    /**
     * Customer temporarily suspended (can be reactivated)
     */
    SUSPENDED,

    /**
     * Customer inactive (dormant account)
     */
    INACTIVE,

    /**
     * Customer permanently closed
     */
    CLOSED,

    /**
     * Customer rejected during approval process
     */
    REJECTED;

    public boolean canTransact() {
        return this == ACTIVE;
    }

    public boolean canBeApproved() {
        return this == PENDING_APPROVAL;
    }

    public boolean canBeActivated() {
        return this == APPROVED;
    }

    public boolean canBeSuspended() {
        return this == ACTIVE;
    }

    public boolean canBeReactivated() {
        return this == SUSPENDED || this == INACTIVE;
    }

    public boolean isClosed() {
        return this == CLOSED;
    }
}

