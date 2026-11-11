package com.tradesphere.ai.lc.domain.model;

/**
 * Letter of credit lifecycle states (REQ-LC-002).
 *
 * <pre>
 * DRAFT -> SUBMITTED -> APPROVED -> ISSUED -> DOCUMENTS_RECEIVED -> DOCUMENTS_ACCEPTED
 *       -> PAYMENT_AUTHORIZED -> PAID -> CLOSED
 * branches: REJECTED, DOCUMENTS_REJECTED, AMENDED, EXPIRED, CANCELLED
 * </pre>
 */
public enum LcStatus {
    DRAFT,
    SUBMITTED,
    APPROVED,
    ISSUED,
    AMENDED,
    DOCUMENTS_RECEIVED,
    DOCUMENTS_REJECTED,
    DOCUMENTS_ACCEPTED,
    PAYMENT_AUTHORIZED,
    PAID,
    CLOSED,
    REJECTED,
    EXPIRED,
    CANCELLED;

    public boolean isTerminal() {
        return this == CLOSED || this == REJECTED || this == EXPIRED || this == CANCELLED;
    }

    /** States in which the credit is live and carries exposure. */
    public boolean isOperative() {
        return switch (this) {
            case ISSUED, AMENDED, DOCUMENTS_RECEIVED, DOCUMENTS_REJECTED, DOCUMENTS_ACCEPTED,
                 PAYMENT_AUTHORIZED, PAID -> true;
            default -> false;
        };
    }
}
