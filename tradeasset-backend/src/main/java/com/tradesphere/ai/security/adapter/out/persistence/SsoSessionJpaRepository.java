package com.tradesphere.ai.security.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for SSO Sessions
 */
@Repository
public interface SsoSessionJpaRepository extends JpaRepository<SsoSessionEntity, UUID> {

    Optional<SsoSessionEntity> findBySessionIdAndTenantId(String sessionId, String tenantId);

    Optional<SsoSessionEntity> findByUserIdAndTenantId(String userId, String tenantId);

    void deleteBySessionIdAndTenantId(String sessionId, String tenantId);

    void deleteByUserIdAndTenantId(String userId, String tenantId);
}

