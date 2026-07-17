package com.tradesphere.ai.compliance.application.service;

import com.tradesphere.ai.compliance.application.port.in.RejectComplianceCheckCommand;
import com.tradesphere.ai.compliance.application.port.in.RejectComplianceCheckUseCase;
import com.tradesphere.ai.compliance.application.port.out.ComplianceCheckRepository;
import com.tradesphere.ai.compliance.application.port.out.PublishComplianceEventPort;
import com.tradesphere.ai.compliance.domain.model.ComplianceCheck;
import com.tradesphere.ai.compliance.domain.model.ComplianceCheckId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case implementation for rejecting compliance checks
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RejectComplianceCheckService implements RejectComplianceCheckUseCase {

    private final ComplianceCheckRepository complianceCheckRepository;
    private final PublishComplianceEventPort eventPublisher;

    @Override
    public void rejectCheck(RejectComplianceCheckCommand command) {
        log.info("Rejecting compliance check: checkId={}, rejectedBy={}",
                command.getComplianceCheckId(), command.getRejectedBy());

        // Load check
        ComplianceCheckId checkId = ComplianceCheckId.of(command.getComplianceCheckId());
        ComplianceCheck check = complianceCheckRepository
                .findById(checkId, command.getTenantId())
                .orElseThrow(() -> new IllegalArgumentException("Compliance check not found"));

        // Reject check
        check.reject(command.getRejectedBy(), command.getReason());

        // Save
        ComplianceCheck updatedCheck = complianceCheckRepository.save(check);

        // Publish domain events
        updatedCheck.getDomainEvents().forEach(eventPublisher::publish);
        updatedCheck.clearDomainEvents();

        log.info("Compliance check rejected: checkId={}, decision={}",
                checkId, check.getDecision());
    }
}

