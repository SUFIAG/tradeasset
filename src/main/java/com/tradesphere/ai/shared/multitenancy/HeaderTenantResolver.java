package com.tradesphere.ai.shared.multitenancy;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Resolves tenant from HTTP header: X-Tenant-ID
 * For production, this would typically come from JWT token or API key
 */
@Slf4j
@Component
public class HeaderTenantResolver implements TenantResolver {

    private static final String TENANT_HEADER = "X-Tenant-ID";

    @Override
    public UUID resolveTenant(HttpServletRequest request) {
        String tenantHeader = request.getHeader(TENANT_HEADER);
        
        if (tenantHeader == null || tenantHeader.trim().isEmpty()) {
            log.warn("No tenant header found in request to: {}", request.getRequestURI());
            throw new IllegalArgumentException("Missing tenant header: " + TENANT_HEADER);
        }

        try {
            UUID tenantId = UUID.fromString(tenantHeader);
            log.debug("Resolved tenant ID: {} for request: {}", tenantId, request.getRequestURI());
            return tenantId;
        } catch (IllegalArgumentException e) {
            log.error("Invalid tenant ID format: {}", tenantHeader);
            throw new IllegalArgumentException("Invalid tenant ID format: " + tenantHeader, e);
        }
    }
}

