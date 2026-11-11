package com.tradesphere.ai.shared.domain.timeline;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * One append-only audit and timeline record (REQ-REL-003, REQ-UI-004): who did what to which
 * record, the state before and after, when, and from which address.
 *
 * @param details structured before/after values or other context. Never put a national ID,
 *                IBAN, passport number, token, OTP or password in here.
 */
public record TimelineEntry(
        UUID id,
        UUID tenantId,
        String entityType,
        UUID entityId,
        String action,
        String fromState,
        String toState,
        UUID actorId,
        String actorName,
        String comment,
        Map<String, Object> details,
        String sourceAddress,
        Instant occurredAt) {

    public TimelineEntry {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(entityType, "entityType");
        Objects.requireNonNull(entityId, "entityId");
        Objects.requireNonNull(action, "action");
        Objects.requireNonNull(occurredAt, "occurredAt");
        details = details == null ? Map.of() : Map.copyOf(details);
    }
}
