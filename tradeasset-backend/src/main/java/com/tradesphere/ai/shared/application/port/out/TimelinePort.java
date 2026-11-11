package com.tradesphere.ai.shared.application.port.out;

import com.tradesphere.ai.shared.domain.timeline.TimelineEntry;
import com.tradesphere.ai.shared.query.PageQuery;
import com.tradesphere.ai.shared.query.PageResult;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Append-only audit trail and per-record timeline (table {@code trade_events}). */
public interface TimelinePort {

    void append(TimelineEntry entry);

    /** Oldest first. */
    List<TimelineEntry> listFor(UUID tenantId, String entityType, UUID entityId);

    /** Tenant-wide audit search for auditors. Every filter is optional. Sort properties: occurredAt. */
    PageResult<TimelineEntry> search(UUID tenantId, AuditFilter filter, PageQuery page);

    record AuditFilter(String entityType, UUID entityId, UUID actorId, String action, Instant from, Instant to) {
    }
}
