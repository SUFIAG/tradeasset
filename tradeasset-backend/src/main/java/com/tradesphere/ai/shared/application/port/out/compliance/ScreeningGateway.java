package com.tradesphere.ai.shared.application.port.out.compliance;

import java.time.Instant;
import java.util.UUID;

/**
 * Cross-module sanctions, PEP and adverse-media screening (REQ-CMP-001..003). Implemented by the
 * compliance module. A hit blocks until a compliance officer dispositions it; nothing clears it
 * automatically.
 */
public interface ScreeningGateway {

    /** Runs screening synchronously, stores the result immutably and returns it. */
    ScreeningResult screen(ScreeningRequest request);

    /**
     * Aggregate clearance of every screening run in a transaction context, for example all the
     * parties of one LC. NOT_SCREENED when nothing was screened in the context.
     */
    Clearance clearanceForContext(UUID tenantId, String contextType, UUID contextId);

    /** Clearance of the latest screening of a subject (for example a customer at onboarding). */
    Clearance clearanceForSubject(UUID tenantId, UUID subjectId);

    /**
     * @param subjectType CUSTOMER, COUNTERPARTY, BANK, VESSEL or GOODS
     * @param subjectId   the customer ID when the subject is a customer; null for a free-text party
     * @param country     ISO 3166-1 alpha-2, may be null
     * @param contextType the record that triggered screening, an {@code EntityTypes} code
     */
    record ScreeningRequest(UUID tenantId, String subjectType, UUID subjectId, String subjectName, String country,
                            String contextType, UUID contextId, String contextReference, UUID requestedBy) {
    }

    /**
     * @param status CLEAR or POTENTIAL_MATCH
     */
    record ScreeningResult(UUID screeningId, String status, int hitCount) {
        public boolean isClear() {
            return "CLEAR".equals(status);
        }
    }

    /**
     * @param status CLEAR, BLOCKED or NOT_SCREENED
     */
    record Clearance(String status, int openHits, Instant lastScreenedAt) {
        public boolean isClear() {
            return "CLEAR".equals(status);
        }
    }
}
