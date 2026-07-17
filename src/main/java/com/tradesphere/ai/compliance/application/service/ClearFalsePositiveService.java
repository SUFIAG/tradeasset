package com.tradesphere.ai.compliance.application.service;

import com.tradesphere.ai.compliance.application.port.in.ClearFalsePositiveCommand;
import com.tradesphere.ai.compliance.application.port.in.ClearFalsePositiveUseCase;
import com.tradesphere.ai.compliance.application.port.out.ComplianceCheckRepository;
import com.tradesphere.ai.compliance.application.port.out.PublishComplianceEventPort;
import com.tradesphere.ai.compliance.domain.model.ComplianceCheck;
import com.tradesphere.ai.compliance.domain.model.ComplianceCheckId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case implementation for clearing false positives
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClearFalsePositiveService implements ClearFalsePositiveUseCase {

    private final ComplianceCheckRepository complianceCheckRepository;
    private final PublishComplianceEventPort eventPublisher;

    @Override
    public void clearAsFalsePositive(ClearFalsePositiveCommand command) {
        log.info("Clearing compliance check as false positive: checkId={}, clearedBy={}",
                command.getComplianceCheckId(), command.getClearedBy());

        // Load check
        ComplianceCheckId checkId = ComplianceCheckId.of(command.getComplianceCheckId());
        ComplianceCheck check = complianceCheckRepository
                .findById(checkId, command.getTenantId())
                .orElseThrow(() -> new IllegalArgumentException("Compliance check not found"));

        // Clear as false positive
        check.clearAsFalsePositive(command.getClearedBy(), command.getReason());

        // Save
        ComplianceCheck updatedCheck = complianceCheckRepository.save(check);

        // Publish domain events
        updatedCheck.getDomainEvents().forEach(eventPublisher::publish);
        updatedCheck.clearDomainEvents();

        log.info("Compliance check cleared as false positive: checkId={}, decision={}",
                checkId, check.getDecision());
    }
}

