package com.tradesphere.ai.shared.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for aggregate roots
 * Aggregates are clusters of domain objects that can be treated as a single unit
 * All changes to objects within the aggregate boundary must go through the aggregate root
 */
@Getter
public abstract class AggregateRoot<ID> extends Entity<ID> {

    private final transient List<DomainEvent> domainEvents = new ArrayList<>();

    protected AggregateRoot(ID id) {
        super(id);
    }

    /**
     * Register a domain event to be published after the aggregate is persisted
     */
    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    /**
     * Get all domain events (immutable view)
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * Clear all domain events after they have been published
     */
    public void clearDomainEvents() {
        domainEvents.clear();
    }

    /**
     * Check if there are any pending domain events
     */
    public boolean hasDomainEvents() {
        return !domainEvents.isEmpty();
    }
}

