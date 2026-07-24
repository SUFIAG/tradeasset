package com.tradesphere.ai.shared.multitenancy;

import java.util.UUID;

/**
 * Thread-safe tenant context holder using ThreadLocal
 * Stores the current tenant ID for the request/transaction
 */
public class TenantContext {

    private static final ThreadLocal<UUID> currentTenant = new InheritableThreadLocal<>();

    private TenantContext() {
        // Private constructor to prevent instantiation
    }

    /**
     * Set the current tenant ID for this thread
     */
    public static void setTenantId(UUID tenantId) {
        if (tenantId == null) {
            throw new IllegalArgumentException("Tenant ID cannot be null");
        }
        currentTenant.set(tenantId);
    }

    /**
     * Get the current tenant ID for this thread
     */
    public static UUID getTenantId() {
        UUID tenantId = currentTenant.get();
        if (tenantId == null) {
            throw new IllegalStateException("No tenant context set for current thread");
        }
        return tenantId;
    }

    /**
     * Get the current tenant ID or null if not set
     */
    public static UUID getTenantIdOrNull() {
        return currentTenant.get();
    }

    /**
     * Check if tenant context is set
     */
    public static boolean isSet() {
        return currentTenant.get() != null;
    }

    /**
     * Clear the tenant context for this thread
     * Should be called in finally block or filter to prevent memory leaks
     */
    public static void clear() {
        currentTenant.remove();
    }
}

