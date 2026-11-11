package com.tradesphere.ai.shared.application.event;

import java.util.UUID;

/**
 * In-process events that let modules react to each other without depending on each other.
 * Publish with Spring's {@code ApplicationEventPublisher}; listen with
 * {@code @TransactionalEventListener} (after commit) unless the reaction must share the
 * publisher's transaction.
 */
public final class IntegrationEvents {

    private IntegrationEvents() {
    }

    /**
     * A compliance officer dispositioned a screening hit (compliance module).
     *
     * @param subjectClearance  CLEAR or BLOCKED: the subject's clearance after the decision
     * @param contextClearance  CLEAR or BLOCKED: the clearance of the whole transaction context
     */
    public record ScreeningDispositioned(UUID tenantId, UUID screeningId, UUID subjectId, String contextType,
                                         UUID contextId, String subjectClearance, String contextClearance) {
    }

    /** A sanctions, PEP or adverse-media list changed; subjects must be re-screened (REQ-CUS-008). */
    public record WatchlistUpdated(UUID tenantId, String listCode) {
    }

    /**
     * The customer's risk rating changed through an assessment or an override (risk module).
     *
     * @param rating LOW, MEDIUM, HIGH or CRITICAL
     */
    public record RiskRatingChanged(UUID tenantId, UUID customerId, String rating, String score, UUID assessmentId) {
    }

    /**
     * A customer moved through its lifecycle (customer module).
     *
     * @param status the new status
     */
    public record CustomerStatusChanged(UUID tenantId, UUID customerId, String status) {
    }
}
