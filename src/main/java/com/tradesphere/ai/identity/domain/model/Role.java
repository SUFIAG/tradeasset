package com.tradesphere.ai.identity.domain.model;

import com.tradesphere.ai.shared.domain.Entity;
import lombok.Getter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Role Entity
 * Represents a collection of permissions that can be assigned to users
 */
@Getter
public class Role extends Entity<UUID> {

    private UUID tenantId;
    private String name;
    private String code; // Unique code: ADMIN, CUSTOMER_MANAGER, etc.
    private String description;
    private Set<UUID> permissionIds;
    private boolean systemRole; // Cannot be deleted if true

    public Role(UUID id, UUID tenantId, String name, String code) {
        super(id);
        this.tenantId = tenantId;
        this.name = name;
        this.code = code;
        this.permissionIds = new HashSet<>();
        this.systemRole = false;
        this.createdAt = Instant.now();
    }

    public void assignPermission(UUID permissionId) {
        permissionIds.add(permissionId);
    }

    public void revokePermission(UUID permissionId) {
        permissionIds.remove(permissionId);
    }

    public boolean hasPermission(UUID permissionId) {
        return permissionIds.contains(permissionId);
    }

    public void markAsSystemRole() {
        this.systemRole = true;
    }
}

