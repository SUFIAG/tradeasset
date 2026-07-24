package com.tradesphere.ai.shared.domain;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Abstract base class for domain events providing common functionality
 */
@Getter
public abstract class BaseDomainEvent implements DomainEvent {

    private final UUID eventId;
    private final String aggregateId;
    private final String aggregateType;
    private final Instant occurredAt;
    private final Integer version;
    private final UUID causedBy;
    private final String correlationId;

    protected BaseDomainEvent(
            String aggregateId,
            String aggregateType,
            UUID causedBy) {
        this.eventId = UUID.randomUUID();
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.occurredAt = Instant.now();
        this.version = 1;
        this.causedBy = causedBy;
        this.correlationId = UUID.randomUUID().toString();
    }

    protected BaseDomainEvent(
            String aggregateId,
            String aggregateType) {
        this(aggregateId, aggregateType, null);
    }

    /**
     * Get event type name (class simple name)
     */
    public String getEventType() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return String.format("%s{eventId=%s, aggregateId=%s, occurredAt=%s}",
                getEventType(), eventId, aggregateId, occurredAt);
    }
}

