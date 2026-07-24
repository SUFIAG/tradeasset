package com.tradesphere.ai.identity.domain.model;

import com.tradesphere.ai.shared.domain.Entity;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Permission Entity
 * Represents a specific authorization to perform an action
 */
@Getter
public class Permission extends Entity<UUID> {

    private String name;
    private String code; // Unique code: CUSTOMER_CREATE, LC_APPROVE, etc.
    private String resource; // Resource: CUSTOMER, LC, PAYMENT, etc.
    private String action; // Action: CREATE, READ, UPDATE, DELETE, APPROVE, etc.
    private String description;

    public Permission(UUID id, String name, String code, String resource, String action) {
        super(id);
        this.name = name;
        this.code = code;
        this.resource = resource;
        this.action = action;
        this.createdAt = Instant.now();
    }

    public String getFullCode() {
        return resource + ":" + action;
    }
}

