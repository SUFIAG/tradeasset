package com.tradesphere.ai.shared.application.service;

import com.tradesphere.ai.shared.application.port.out.TimelinePort;
import com.tradesphere.ai.shared.domain.timeline.TimelineEntry;
import com.tradesphere.ai.shared.security.AuthenticatedUser;
import com.tradesphere.ai.shared.security.RequestMetadata;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Writes the audit trail (REQ-REL-003). Every state change of every record goes through here,
 * inside the same transaction as the change itself, so the trail can never disagree with the data.
 */
@Service
public class AuditTrail {

    private final TimelinePort timeline;
    private final Clock clock;

    public AuditTrail(TimelinePort timeline, Clock clock) {
        this.timeline = timeline;
        this.clock = clock;
    }

    /** Records an action by a signed-in user. */
    public TimelineEntry record(AuthenticatedUser actor, String entityType, UUID entityId, String action,
                                String fromState, String toState, String comment, Map<String, Object> details) {
        TimelineEntry entry = new TimelineEntry(UUID.randomUUID(), actor.tenantId(), entityType, entityId, action,
                fromState, toState, actor.userId(), actor.displayName(), comment, details,
                RequestMetadata.clientIp(), clock.instant());
        timeline.append(entry);
        return entry;
    }

    /** Records an action taken by the system itself (scheduler, expiry job, reconciliation). */
    public TimelineEntry recordSystem(UUID tenantId, String entityType, UUID entityId, String action,
                                      String fromState, String toState, String comment, Map<String, Object> details) {
        TimelineEntry entry = new TimelineEntry(UUID.randomUUID(), tenantId, entityType, entityId, action,
                fromState, toState, null, "SYSTEM", comment, details, null, clock.instant());
        timeline.append(entry);
        return entry;
    }

    /**
     * Records an action by a known user outside an HTTP request, or before a security context exists
     * (for example a failed login).
     */
    public TimelineEntry recordFor(UUID tenantId, UUID actorId, String actorName, String entityType, UUID entityId,
                                   String action, String fromState, String toState, String comment,
                                   Map<String, Object> details) {
        TimelineEntry entry = new TimelineEntry(UUID.randomUUID(), tenantId, entityType, entityId, action,
                fromState, toState, actorId, actorName, comment, details, RequestMetadata.clientIp(),
                clock.instant());
        timeline.append(entry);
        return entry;
    }

    public List<TimelineEntry> timelineOf(UUID tenantId, String entityType, UUID entityId) {
        return timeline.listFor(tenantId, entityType, entityId);
    }
}
