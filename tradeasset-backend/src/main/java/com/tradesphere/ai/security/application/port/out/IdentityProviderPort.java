package com.tradesphere.ai.security.application.port.out;

import com.tradesphere.ai.security.domain.model.IdentityProvider;

import java.util.List;
import java.util.Optional;

/**
 * Port for Identity Provider persistence
 */
public interface IdentityProviderPort {

    /**
     * Save identity provider
     */
    IdentityProvider save(IdentityProvider provider);

    /**
     * Find provider by ID
     */
    Optional<IdentityProvider> findById(String providerId, String tenantId);

    /**
     * Find all active providers for tenant
     */
    List<IdentityProvider> findAllActive(String tenantId);

    /**
     * Update provider
     */
    IdentityProvider update(IdentityProvider provider);

    /**
     * Delete provider
     */
    void delete(String providerId, String tenantId);
}

