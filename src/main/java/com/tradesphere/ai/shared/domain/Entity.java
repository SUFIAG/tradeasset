package com.tradesphere.ai.shared.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Base class for all entities in the domain model
 * Entities have identity and lifecycle
 */
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public abstract class Entity<ID> {

    protected ID id;
    protected Long version;
    protected Instant createdAt;
    protected UUID createdBy;
    protected Instant updatedAt;
    protected UUID updatedBy;
    protected Instant deletedAt;
    protected UUID deletedBy;

    protected Entity(ID id) {
        this.id = id;
        this.version = 1L;
        this.createdAt = Instant.now();
    }

    /**
     * Soft delete support
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * Mark entity as deleted (soft delete)
     */
    public void markAsDeleted(UUID deletedBy) {
        this.deletedAt = Instant.now();
        this.deletedBy = deletedBy;
    }

    /**
     * Update tracking
     */
    protected void markAsUpdated(UUID updatedBy) {
        this.updatedAt = Instant.now();
        this.updatedBy = updatedBy;
        this.version++;
    }
}

