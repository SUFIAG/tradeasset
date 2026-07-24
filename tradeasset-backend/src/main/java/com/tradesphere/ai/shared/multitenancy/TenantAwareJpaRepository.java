package com.tradesphere.ai.shared.multitenancy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Base repository interface for tenant-aware entities
 * Automatically filters by tenant ID for high-performance multi-tenancy
 */
@NoRepositoryBean
public interface TenantAwareJpaRepository<T, ID> extends JpaRepository<T, ID> {

    /**
     * Find by ID within current tenant context
     */
    Optional<T> findByIdAndTenantId(ID id, UUID tenantId);

    /**
     * Find all entities for current tenant
     */
    List<T> findAllByTenantId(UUID tenantId);

    /**
     * Delete by ID within tenant context (prevents cross-tenant deletion)
     */
    void deleteByIdAndTenantId(ID id, UUID tenantId);

    /**
     * Check existence within tenant context
     */
    boolean existsByIdAndTenantId(ID id, UUID tenantId);
}

