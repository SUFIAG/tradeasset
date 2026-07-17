package com.tradesphere.ai.shared.web;

import java.util.UUID;

/**
 * Tenant Context Utility
 * Provides tenant-scoped context for multi-tenancy support
 */
public class TenantContext {

    private static final ThreadLocal<UUID> currentTenant = new ThreadLocal<>();

    /**
     * Set the current tenant ID for this thread
     */
    public static void setCurrentTenant(UUID tenantId) {
        currentTenant.set(tenantId);
    }

    /**
     * Get the current tenant ID
     */
    public static UUID getCurrentTenant() {
        return currentTenant.get();
    }

    /**
     * Clear the tenant context
     */
    public static void clear() {
        currentTenant.remove();
    }

    /**
     * Check if tenant context is set
     */
    public static boolean hasTenant() {
        return currentTenant.get() != null;
    }
}

