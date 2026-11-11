package com.tradesphere.ai.lc.domain.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static com.tradesphere.ai.lc.domain.model.LcAction.*;
import static com.tradesphere.ai.lc.domain.model.LcStatus.*;

/**
 * The single, explicit state machine for letters of credit (REQ-STA-001).
 *
 * <p>Each entry maps (current state, action) to the resulting state. Actions that record
 * something without moving the credit (raising a discrepancy, requesting an amendment) map to
 * the current state. Anything absent from the table is illegal and is rejected with the current
 * state and the legal alternatives.</p>
 */
public final class LcStateMachine {

    private static final Map<LcStatus, Map<LcAction, LcStatus>> TABLE = new EnumMap<>(LcStatus.class);

    static {
        on(DRAFT, UPDATE_DRAFT, DRAFT);
        on(DRAFT, SUBMIT, SUBMITTED);
        on(DRAFT, CANCEL, CANCELLED);

        on(SUBMITTED, APPROVE, APPROVED);
        on(SUBMITTED, REJECT, REJECTED);
        on(SUBMITTED, CANCEL, CANCELLED);

        on(APPROVED, ISSUE, ISSUED);
        on(APPROVED, REJECT, REJECTED);
        on(APPROVED, CANCEL, CANCELLED);

        for (LcStatus live : new LcStatus[]{ISSUED, AMENDED}) {
            on(live, PRESENT_DOCUMENTS, DOCUMENTS_RECEIVED);
            on(live, REQUEST_AMENDMENT, live);
            on(live, CONSENT_AMENDMENT, AMENDED);
            on(live, REJECT_AMENDMENT, live);
            on(live, CANCEL, CANCELLED);
            on(live, EXPIRE, EXPIRED);
        }

        on(DOCUMENTS_RECEIVED, EXAMINE_DOCUMENTS, DOCUMENTS_RECEIVED);
        on(DOCUMENTS_RECEIVED, RAISE_DISCREPANCY, DOCUMENTS_RECEIVED);
        on(DOCUMENTS_RECEIVED, WITHDRAW_DISCREPANCY, DOCUMENTS_RECEIVED);
        on(DOCUMENTS_RECEIVED, ACCEPT_DOCUMENTS, DOCUMENTS_ACCEPTED);
        on(DOCUMENTS_RECEIVED, REFUSE_DOCUMENTS, DOCUMENTS_REJECTED);
        on(DOCUMENTS_RECEIVED, WAIVE_DISCREPANCIES, DOCUMENTS_ACCEPTED);

        on(DOCUMENTS_REJECTED, WAIVE_DISCREPANCIES, DOCUMENTS_ACCEPTED);
        on(DOCUMENTS_REJECTED, PRESENT_DOCUMENTS, DOCUMENTS_RECEIVED);
        on(DOCUMENTS_REJECTED, EXPIRE, EXPIRED);

        on(DOCUMENTS_ACCEPTED, REQUEST_PAYMENT, DOCUMENTS_ACCEPTED);
        on(DOCUMENTS_ACCEPTED, REJECT_PAYMENT, DOCUMENTS_ACCEPTED);
        on(DOCUMENTS_ACCEPTED, AUTHORIZE_PAYMENT, PAYMENT_AUTHORIZED);

        on(PAYMENT_AUTHORIZED, CONFIRM_PAYMENT, PAID);

        // A partial drawing leaves the credit open for the next presentation (REQ-LC-009).
        on(PAID, PRESENT_DOCUMENTS, DOCUMENTS_RECEIVED);
        on(PAID, REQUEST_AMENDMENT, PAID);
        on(PAID, CONSENT_AMENDMENT, AMENDED);
        on(PAID, REJECT_AMENDMENT, PAID);
        on(PAID, CLOSE, CLOSED);
        on(PAID, EXPIRE, EXPIRED);
    }

    private LcStateMachine() {
    }

    private static void on(LcStatus from, LcAction action, LcStatus to) {
        TABLE.computeIfAbsent(from, k -> new EnumMap<>(LcAction.class)).put(action, to);
    }

    /** Returns the resulting state, or throws a typed error naming the current state and legal actions. */
    public static LcStatus next(LcStatus current, LcAction action) {
        LcStatus to = TABLE.getOrDefault(current, Map.of()).get(action);
        if (to == null) {
            throw LcException.illegalTransition(current, action, legalActions(current), legalNextStates(current));
        }
        return to;
    }

    public static boolean isLegal(LcStatus current, LcAction action) {
        return TABLE.getOrDefault(current, Map.of()).containsKey(action);
    }

    public static Set<LcAction> legalActions(LcStatus current) {
        Map<LcAction, LcStatus> row = TABLE.get(current);
        return row == null ? Collections.emptySet() : Collections.unmodifiableSet(row.keySet());
    }

    public static Set<LcStatus> legalNextStates(LcStatus current) {
        Map<LcAction, LcStatus> row = TABLE.get(current);
        if (row == null) {
            return Collections.emptySet();
        }
        Set<LcStatus> next = new LinkedHashSet<>();
        row.forEach((action, to) -> {
            if (to != current) {
                next.add(to);
            }
        });
        return next;
    }
}
