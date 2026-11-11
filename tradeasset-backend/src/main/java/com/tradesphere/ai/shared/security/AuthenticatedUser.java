package com.tradesphere.ai.shared.security;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

/**
 * The verified caller, built only from a validated access token (REQ-SEC-002). Never construct
 * one from request headers or body fields.
 *
 * @param customerId set only for CUSTOMER_USER principals: the customer they act for
 */
public record AuthenticatedUser(
        UUID userId,
        UUID tenantId,
        String username,
        String displayName,
        Set<String> roles,
        Set<String> permissions,
        UUID customerId) {

    public AuthenticatedUser {
        roles = roles == null ? Set.of() : Set.copyOf(roles);
        permissions = permissions == null ? Set.of() : Set.copyOf(permissions);
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    public boolean hasAnyRole(Collection<String> candidates) {
        for (String r : candidates) {
            if (roles.contains(r)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAnyRole(String... candidates) {
        return hasAnyRole(Set.of(candidates));
    }

    /** True when the principal is an external customer user and must be scoped to their customer. */
    public boolean isCustomerUser() {
        return roles.contains(Roles.CUSTOMER_USER) && roles.size() == 1;
    }
}
