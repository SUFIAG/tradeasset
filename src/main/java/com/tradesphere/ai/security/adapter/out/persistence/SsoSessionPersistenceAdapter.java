package com.tradesphere.ai.security.adapter.out.persistence;

import com.tradesphere.ai.security.application.port.out.SsoSessionPort;
import com.tradesphere.ai.security.domain.model.SsoSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Persistence adapter for SSO Sessions
 */
@Component
@RequiredArgsConstructor
public class SsoSessionPersistenceAdapter implements SsoSessionPort {

    private final SsoSessionJpaRepository repository;
    private final SsoSessionMapper mapper;

    @Override
    @Transactional
    public SsoSession save(SsoSession session) {
        SsoSessionEntity entity = mapper.toEntity(session);
        SsoSessionEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SsoSession> findBySessionId(String sessionId, String tenantId) {
        return repository.findBySessionIdAndTenantId(sessionId, tenantId)
            .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SsoSession> findByUserId(String userId, String tenantId) {
        return repository.findByUserIdAndTenantId(userId, tenantId)
            .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void delete(String sessionId, String tenantId) {
        repository.deleteBySessionIdAndTenantId(sessionId, tenantId);
    }

    @Override
    @Transactional
    public void deleteByUserId(String userId, String tenantId) {
        repository.deleteByUserIdAndTenantId(userId, tenantId);
    }

    @Override
    @Transactional
    public SsoSession update(SsoSession session) {
        SsoSessionEntity entity = mapper.toEntity(session);
        SsoSessionEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }
}

