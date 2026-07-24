package com.tradesphere.ai.risk.adapter.out.persistence;

import com.tradesphere.ai.risk.application.port.out.RiskAssessmentRepository;
import com.tradesphere.ai.risk.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter for risk assessment persistence
 */
@Component
@RequiredArgsConstructor
public class RiskAssessmentRepositoryAdapter implements RiskAssessmentRepository {

    private final RiskAssessmentJpaRepository jpaRepository;
    private final RiskAssessmentMapper mapper;

    @Override
    public RiskAssessment save(RiskAssessment assessment) {
        RiskAssessmentJpaEntity entity = mapper.toJpaEntity(assessment);
        RiskAssessmentJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomainModel(saved);
    }

    @Override
    public Optional<RiskAssessment> findById(RiskAssessmentId assessmentId, UUID tenantId) {
        return jpaRepository.findByIdAndTenantId(assessmentId.getValue(), tenantId)
                .map(mapper::toDomainModel);
    }

    @Override
    public List<RiskAssessment> findByEntity(String entityType, UUID entityId, UUID tenantId) {
        return jpaRepository.findAllByEntityTypeAndEntityIdAndTenantId(entityType, entityId, tenantId)
                .stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskAssessment> findHighRiskAssessments(UUID tenantId) {
        return jpaRepository.findHighRiskAssessments(tenantId)
                .stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskAssessment> findByStatus(RiskAssessmentStatus status, UUID tenantId) {
        return jpaRepository.findByStatusAndTenantId(status, tenantId)
                .stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }
}

