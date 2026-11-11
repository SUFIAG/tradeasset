package com.tradesphere.ai.lc.domain.model;

/**
 * Every command that can be applied to a letter of credit. The state machine decides which
 * of these are legal in a given state; the action policy then filters by role and four-eyes rules.
 */
public enum LcAction {
    UPDATE_DRAFT,
    SUBMIT,
    APPROVE,
    REJECT,
    ISSUE,
    CANCEL,
    REQUEST_AMENDMENT,
    CONSENT_AMENDMENT,
    REJECT_AMENDMENT,
    PRESENT_DOCUMENTS,
    EXAMINE_DOCUMENTS,
    RAISE_DISCREPANCY,
    WITHDRAW_DISCREPANCY,
    ACCEPT_DOCUMENTS,
    REFUSE_DOCUMENTS,
    WAIVE_DISCREPANCIES,
    REQUEST_PAYMENT,
    AUTHORIZE_PAYMENT,
    REJECT_PAYMENT,
    CONFIRM_PAYMENT,
    CLOSE,
    EXPIRE
}
