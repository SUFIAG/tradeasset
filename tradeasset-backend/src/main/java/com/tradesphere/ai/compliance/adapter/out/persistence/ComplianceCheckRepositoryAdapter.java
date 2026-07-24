package com.tradesphere.ai.compliance.adapter.out.persistence;

import com.tradesphere.ai.compliance.application.port.out.ComplianceCheckRepository;
import com.tradesphere.ai.compliance.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter for compliance check persistence
 * Implements the repository port using JPA
 */
@Component
@RequiredArgsConstructor
public class ComplianceCheckRepositoryAdapter implements ComplianceCheckRepository {

    private final ComplianceCheckJpaRepository jpaRepository;
    private final ComplianceCheckMapper mapper;

    @Override
    public ComplianceCheck save(ComplianceCheck complianceCheck) {
        ComplianceCheckJpaEntity entity = mapper.toJpaEntity(complianceCheck);
        ComplianceCheckJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomainModel(saved);
    }

    @Override
    public Optional<ComplianceCheck> findById(ComplianceCheckId checkId, UUID tenantId) {
        return jpaRepository.findByIdAndTenantId(checkId.getValue(), tenantId)
                .map(mapper::toDomainModel);
    }

    @Override
    public Optional<ComplianceCheck> findByEntity(String entityType, UUID entityId, UUID tenantId) {
        return jpaRepository.findByEntityTypeAndEntityIdAndTenantId(entityType, entityId, tenantId)
                .map(mapper::toDomainModel);
    }

    @Override
    public List<ComplianceCheck> findAllByEntity(String entityType, UUID entityId, UUID tenantId) {
        return jpaRepository.findAllByEntityTypeAndEntityIdAndTenantId(entityType, entityId, tenantId)
                .stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ComplianceCheck> findChecksRequiringReview(UUID tenantId) {
        return jpaRepository.findChecksRequiringReview(tenantId)
                .stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ComplianceCheck> findByStatus(ComplianceCheckStatus status, UUID tenantId) {
        return jpaRepository.findByStatusAndTenantId(status, tenantId)
                .stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ComplianceCheck> findByDecision(ComplianceDecision decision, UUID tenantId) {
        return jpaRepository.findByDecisionAndTenantId(decision, tenantId)
                .stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ComplianceCheck> findRecentChecks(UUID tenantId, Instant since) {
        LocalDateTime sinceLocal = LocalDateTime.ofInstant(since, ZoneId.systemDefault());
        return jpaRepository.findRecentChecks(sinceLocal, tenantId)
                .stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasPassedCompliance(String entityType, UUID entityId, UUID tenantId) {
        return jpaRepository.hasPassedCompliance(entityType, entityId, tenantId);
    }

    @Override
    public boolean exists(ComplianceCheckId checkId, UUID tenantId) {
        return jpaRepository.findByIdAndTenantId(checkId.getValue(), tenantId).isPresent();
    }
}

