package com.tradesphere.ai.security.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for MFA Setup
 */
@Repository
public interface MfaSetupJpaRepository extends JpaRepository<MfaSetupEntity, UUID> {
    
    Optional<MfaSetupEntity> findByUserIdAndTenantId(String userId, String tenantId);
    
    void deleteByUserIdAndTenantId(String userId, String tenantId);
    
    boolean existsByUserIdAndTenantIdAndEnabledTrue(String userId, String tenantId);
}

