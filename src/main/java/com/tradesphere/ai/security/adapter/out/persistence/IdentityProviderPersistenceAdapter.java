package com.tradesphere.ai.security.adapter.out.persistence;

import com.tradesphere.ai.security.application.port.out.IdentityProviderPort;
import com.tradesphere.ai.security.domain.model.IdentityProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Persistence adapter for Identity Providers
 */
@Component
@RequiredArgsConstructor
public class IdentityProviderPersistenceAdapter implements IdentityProviderPort {

    private final IdentityProviderJpaRepository repository;
    private final IdentityProviderMapper mapper;

    @Override
    @Transactional
    public IdentityProvider save(IdentityProvider provider) {
        IdentityProviderEntity entity = mapper.toEntity(provider);
        IdentityProviderEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IdentityProvider> findById(String providerId, String tenantId) {
        return repository.findByProviderIdAndTenantId(providerId, tenantId)
            .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdentityProvider> findAllActive(String tenantId) {
        return repository.findByTenantIdAndActiveTrue(tenantId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public IdentityProvider update(IdentityProvider provider) {
        IdentityProviderEntity entity = mapper.toEntity(provider);
        IdentityProviderEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }

    @Override
    @Transactional
    public void delete(String providerId, String tenantId) {
        repository.deleteByProviderIdAndTenantId(providerId, tenantId);
    }
}

