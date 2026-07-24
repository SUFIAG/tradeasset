package com.tradesphere.ai.security.adapter.out.persistence;

import com.tradesphere.ai.security.application.port.out.MfaSetupPort;
import com.tradesphere.ai.security.domain.model.MfaSetup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Persistence adapter for MFA Setup
 */
@Component
@RequiredArgsConstructor
public class MfaSetupPersistenceAdapter implements MfaSetupPort {
    
    private final MfaSetupJpaRepository repository;
    private final MfaSetupMapper mapper;
    
    @Override
    @Transactional
    public MfaSetup save(MfaSetup setup) {
        MfaSetupEntity entity = mapper.toEntity(setup);
        MfaSetupEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<MfaSetup> findByUserId(String userId, String tenantId) {
        return repository.findByUserIdAndTenantId(userId, tenantId)
            .map(mapper::toDomain);
    }
    
    @Override
    @Transactional
    public MfaSetup update(MfaSetup setup) {
        MfaSetupEntity entity = mapper.toEntity(setup);
        MfaSetupEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }
    
    @Override
    @Transactional
    public void delete(String userId, String tenantId) {
        repository.deleteByUserIdAndTenantId(userId, tenantId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isMfaEnabled(String userId, String tenantId) {
        return repository.existsByUserIdAndTenantIdAndEnabledTrue(userId, tenantId);
    }
}

