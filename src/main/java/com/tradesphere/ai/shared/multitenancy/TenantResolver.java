package com.tradesphere.ai.shared.multitenancy;

import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

/**
 * Interface for resolving tenant from HTTP request
 */
public interface TenantResolver {

    /**
     * Resolve tenant ID from HTTP request
     * @param request HTTP request
     * @return Tenant ID
     * @throws IllegalArgumentException if tenant cannot be resolved
     */
    UUID resolveTenant(HttpServletRequest request);
}

