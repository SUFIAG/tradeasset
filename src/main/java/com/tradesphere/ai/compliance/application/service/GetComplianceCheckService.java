package com.tradesphere.ai.compliance.application.service;

import com.tradesphere.ai.compliance.application.port.in.GetComplianceCheckUseCase;
import com.tradesphere.ai.compliance.application.port.out.ComplianceCheckRepository;
import com.tradesphere.ai.compliance.domain.model.ComplianceCheck;
import com.tradesphere.ai.compliance.domain.model.ComplianceCheckId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Use case implementation for querying compliance checks
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetComplianceCheckService implements GetComplianceCheckUseCase {

    private final ComplianceCheckRepository complianceCheckRepository;

    @Override
    public ComplianceCheck getById(UUID complianceCheckId, UUID tenantId) {
        log.debug("Getting compliance check: checkId={}, tenantId={}", complianceCheckId, tenantId);

        ComplianceCheckId checkId = ComplianceCheckId.of(complianceCheckId);
        return complianceCheckRepository
                .findById(checkId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Compliance check not found"));
    }

    @Override
    public List<ComplianceCheck> getByEntity(String entityType, UUID entityId, UUID tenantId) {
        log.debug("Getting compliance checks for entity: entityType={}, entityId={}, tenantId={}",
                entityType, entityId, tenantId);

        return complianceCheckRepository.findAllByEntity(entityType, entityId, tenantId);
    }

    @Override
    public List<ComplianceCheck> getChecksRequiringReview(UUID tenantId) {
        log.debug("Getting compliance checks requiring review: tenantId={}", tenantId);

        return complianceCheckRepository.findChecksRequiringReview(tenantId);
    }

    @Override
    public boolean hasPassedCompliance(String entityType, UUID entityId, UUID tenantId) {
        log.debug("Checking if entity has passed compliance: entityType={}, entityId={}, tenantId={}",
                entityType, entityId, tenantId);

        return complianceCheckRepository.hasPassedCompliance(entityType, entityId, tenantId);
    }
}

