package com.tradesphere.ai.security.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for Identity Providers
 */
@Repository
public interface IdentityProviderJpaRepository extends JpaRepository<IdentityProviderEntity, UUID> {

    Optional<IdentityProviderEntity> findByProviderIdAndTenantId(String providerId, String tenantId);

    List<IdentityProviderEntity> findByTenantIdAndActiveTrue(String tenantId);

    void deleteByProviderIdAndTenantId(String providerId, String tenantId);
}

