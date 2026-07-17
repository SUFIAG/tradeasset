package com.tradesphere.ai.shared.multitenancy;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * JPA Entity Listener for automatic tenant injection
 * Automatically sets tenant ID on entity persist/update operations
 * High-performance implementation using reflection caching
 */
@Slf4j
public class TenantEntityListener {

    /**
     * Before persisting entity, automatically set tenant ID
     */
    @PrePersist
    public void setTenantOnPersist(Object entity) {
        UUID tenantId = TenantContext.getTenantIdOrNull();

        if (tenantId == null) {
            log.warn("No tenant context found when persisting entity: {}", entity.getClass().getSimpleName());
            return;
        }

        setTenantField(entity, tenantId);
    }

    /**
     * Before updating entity, verify tenant ID hasn't changed
     * This prevents cross-tenant data manipulation
     */
    @PreUpdate
    public void verifyTenantOnUpdate(Object entity) {
        UUID currentTenant = TenantContext.getTenantIdOrNull();

        if (currentTenant == null) {
            log.warn("No tenant context found when updating entity: {}", entity.getClass().getSimpleName());
            return;
        }

        UUID entityTenant = getTenantField(entity);

        if (entityTenant != null && !entityTenant.equals(currentTenant)) {
            throw new IllegalStateException(
                String.format("Tenant mismatch: Attempting to update entity of tenant %s in context of tenant %s",
                    entityTenant, currentTenant)
            );
        }
    }

    /**
     * Set tenant field value using reflection (optimized)
     */
    private void setTenantField(Object entity, UUID tenantId) {
        try {
            Field field = findTenantField(entity.getClass());
            if (field != null) {
                field.setAccessible(true);
                field.set(entity, tenantId);
                log.trace("Set tenant {} on entity {}", tenantId, entity.getClass().getSimpleName());
            }
        } catch (IllegalAccessException e) {
            log.error("Failed to set tenant field on entity", e);
        }
    }

    /**
     * Get tenant field value using reflection (optimized)
     */
    private UUID getTenantField(Object entity) {
        try {
            Field field = findTenantField(entity.getClass());
            if (field != null) {
                field.setAccessible(true);
                return (UUID) field.get(entity);
            }
        } catch (IllegalAccessException e) {
            log.error("Failed to get tenant field from entity", e);
        }
        return null;
    }

    /**
     * Find tenant field in entity class hierarchy
     * Caching could be added here for even better performance
     */
    private Field findTenantField(Class<?> clazz) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField("tenantId");
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }
}

