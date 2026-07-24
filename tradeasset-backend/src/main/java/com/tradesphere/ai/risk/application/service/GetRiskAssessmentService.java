package com.tradesphere.ai.risk.application.service;

import com.tradesphere.ai.risk.application.port.in.GetRiskAssessmentUseCase;
import com.tradesphere.ai.risk.application.port.out.RiskAssessmentRepository;
import com.tradesphere.ai.risk.domain.model.RiskAssessment;
import com.tradesphere.ai.risk.domain.model.RiskAssessmentId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for querying risk assessments
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetRiskAssessmentService implements GetRiskAssessmentUseCase {

    private final RiskAssessmentRepository repository;

    @Override
    public RiskAssessment getById(UUID assessmentId, UUID tenantId) {
        log.debug("Getting risk assessment: assessmentId={}, tenantId={}", assessmentId, tenantId);

        RiskAssessmentId id = RiskAssessmentId.of(assessmentId);
        return repository.findById(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Risk assessment not found"));
    }

    @Override
    public List<RiskAssessment> getByEntity(String entityType, UUID entityId, UUID tenantId) {
        log.debug("Getting risk assessments for entity: entityType={}, entityId={}, tenantId={}",
                entityType, entityId, tenantId);

        return repository.findByEntity(entityType, entityId, tenantId);
    }

    @Override
    public List<RiskAssessment> getHighRiskAssessments(UUID tenantId) {
        log.debug("Getting high risk assessments: tenantId={}", tenantId);

        return repository.findHighRiskAssessments(tenantId);
    }
}

