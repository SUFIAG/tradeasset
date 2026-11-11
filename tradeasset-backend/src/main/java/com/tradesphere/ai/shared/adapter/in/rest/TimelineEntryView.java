package com.tradesphere.ai.shared.adapter.in.rest;

import com.tradesphere.ai.shared.domain.timeline.TimelineEntry;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * A timeline or audit row as returned by the API. {@code sourceAddress} is filled only for
 * auditors and administrators.
 */
public record TimelineEntryView(UUID id, String entityType, UUID entityId, String action, String fromState,
                                String toState, UUID actorId, String actorName, String comment,
                                Map<String, Object> details, String sourceAddress, Instant occurredAt) {

    public static TimelineEntryView of(TimelineEntry e, boolean includeSourceAddress) {
        return new TimelineEntryView(e.id(), e.entityType(), e.entityId(), e.action(), e.fromState(), e.toState(),
                e.actorId(), e.actorName(), e.comment(), e.details(),
                includeSourceAddress ? e.sourceAddress() : null, e.occurredAt());
    }
}
