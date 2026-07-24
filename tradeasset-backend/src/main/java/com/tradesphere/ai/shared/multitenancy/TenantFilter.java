package com.tradesphere.ai.shared.multitenancy;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Filter to resolve and set tenant context for each request
 * High priority filter (Order 1) to ensure tenant is set before any business logic
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class TenantFilter implements Filter {

    private final TenantResolver tenantResolver;

    // Paths that don't require tenant context
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/actuator",
            "/swagger-ui",
            "/v3/api-docs",
            "/health",
            "/error"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestUri = httpRequest.getRequestURI();

        try {
            // Skip tenant resolution for excluded paths
            if (isExcludedPath(requestUri)) {
                chain.doFilter(request, response);
                return;
            }

            // Resolve and set tenant context
            UUID tenantId = tenantResolver.resolveTenant(httpRequest);
            TenantContext.setTenantId(tenantId);

            log.trace("Tenant context set: {} for request: {}", tenantId, requestUri);

            // Continue with the request
            chain.doFilter(request, response);

        } catch (IllegalArgumentException e) {
            log.error("Failed to resolve tenant for request: {}", requestUri, e);
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } finally {
            // Always clear tenant context to prevent memory leaks and cross-request contamination
            TenantContext.clear();
            log.trace("Tenant context cleared for request: {}", requestUri);
        }
    }

    private boolean isExcludedPath(String requestUri) {
        return EXCLUDED_PATHS.stream().anyMatch(requestUri::startsWith);
    }
}

