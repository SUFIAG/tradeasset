package com.tradesphere.ai.compliance.application.service;

import com.tradesphere.ai.compliance.application.port.in.ApproveComplianceCheckCommand;
import com.tradesphere.ai.compliance.application.port.in.ApproveComplianceCheckUseCase;
import com.tradesphere.ai.compliance.application.port.out.ComplianceCheckRepository;
import com.tradesphere.ai.compliance.application.port.out.PublishComplianceEventPort;
import com.tradesphere.ai.compliance.domain.model.ComplianceCheck;
import com.tradesphere.ai.compliance.domain.model.ComplianceCheckId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case implementation for approving compliance checks
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApproveComplianceCheckService implements ApproveComplianceCheckUseCase {

    private final ComplianceCheckRepository complianceCheckRepository;
    private final PublishComplianceEventPort eventPublisher;

    @Override
    public void approveCheck(ApproveComplianceCheckCommand command) {
        log.info("Approving compliance check: checkId={}, approvedBy={}",
                command.getComplianceCheckId(), command.getApprovedBy());

        // Load check
        ComplianceCheckId checkId = ComplianceCheckId.of(command.getComplianceCheckId());
        ComplianceCheck check = complianceCheckRepository
                .findById(checkId, command.getTenantId())
                .orElseThrow(() -> new IllegalArgumentException("Compliance check not found"));

        // Approve check
        check.approve(command.getApprovedBy(), command.getReason());

        // Save
        ComplianceCheck updatedCheck = complianceCheckRepository.save(check);

        // Publish domain events
        updatedCheck.getDomainEvents().forEach(eventPublisher::publish);
        updatedCheck.clearDomainEvents();

        log.info("Compliance check approved: checkId={}, decision={}",
                checkId, check.getDecision());
    }
}

