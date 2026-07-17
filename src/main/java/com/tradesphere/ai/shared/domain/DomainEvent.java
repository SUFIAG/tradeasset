package com.tradesphere.ai.shared.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Base interface for all domain events
 *
 * Domain events represent something that happened in the domain that domain experts care about
 * They are immutable facts about the past
 *
 * Events are published after the aggregate is successfully persisted
 */
public interface DomainEvent extends Serializable {

    /**
     * Unique identifier for this event instance
     */
    UUID getEventId();

    /**
     * ID of the aggregate that produced this event
     */
    String getAggregateId();

    /**
     * Type of the aggregate (e.g., "LetterOfCredit", "Customer")
     */
    String getAggregateType();

    /**
     * Type of the event (e.g., "CustomerCreated", "LCApproved")
     */
    String getEventType();

    /**
     * When this event occurred
     */
    Instant getOccurredAt();

    /**
     * Version of the event schema (for event evolution)
     */
    Integer getVersion();

    /**
     * User or system that caused this event
     */
    default UUID getCausedBy() {
        return null;
    }

    /**
     * Correlation ID for tracing related events
     */
    default String getCorrelationId() {
        return null;
    }
}

