package com.tradesphere.ai.compliance.application.service;

import com.tradesphere.ai.compliance.application.port.in.InitiateComplianceCheckCommand;
import com.tradesphere.ai.compliance.application.port.in.InitiateComplianceCheckUseCase;
import com.tradesphere.ai.compliance.application.port.out.ComplianceCheckRepository;
import com.tradesphere.ai.compliance.application.port.out.ComplianceScreeningPort;
import com.tradesphere.ai.compliance.application.port.out.PublishComplianceEventPort;
import com.tradesphere.ai.compliance.domain.model.ComplianceCheck;
import com.tradesphere.ai.compliance.domain.model.ComplianceCheckId;
import com.tradesphere.ai.compliance.domain.model.ScreeningHit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Use case implementation for initiating compliance checks
 * Integrates AI-powered screening with sanctions, PEP, and AML checks
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InitiateComplianceCheckService implements InitiateComplianceCheckUseCase {

    private final ComplianceCheckRepository complianceCheckRepository;
    private final ComplianceScreeningPort screeningPort;
    private final PublishComplianceEventPort eventPublisher;

    @Override
    public ComplianceCheckId initiateCheck(InitiateComplianceCheckCommand command) {
        log.info("Initiating compliance check: type={}, entityType={}, entityId={}",
                command.getCheckType(), command.getEntityType(), command.getEntityId());

        // Create compliance check
        ComplianceCheck check = ComplianceCheck.create(
                command.getTenantId(),
                command.getCheckType(),
                command.getEntityType(),
                command.getEntityId(),
                command.getEntityName(),
                command.getInitiatedBy()
        );

        // Save initial check
        ComplianceCheck savedCheck = complianceCheckRepository.save(check);

        // Publish domain events
        savedCheck.getDomainEvents().forEach(eventPublisher::publish);
        savedCheck.clearDomainEvents();

        // Perform screening asynchronously (will be handled by event listener)
        log.info("Compliance check initiated: id={}, checkNumber={}",
                savedCheck.getId(), savedCheck.getCheckNumber());

        return savedCheck.getId();
    }

    /**
     * Perform the actual screening (called by event listener or directly for synchronous screening)
     */
    public void performScreening(ComplianceCheckId checkId, InitiateComplianceCheckCommand command) {
        log.info("Performing compliance screening: checkId={}", checkId);

        // Load check
        ComplianceCheck check = complianceCheckRepository
                .findById(checkId, command.getTenantId())
                .orElseThrow(() -> new IllegalArgumentException("Compliance check not found"));

        // Perform sanctions screening
        List<ScreeningHit> sanctionsHits = screeningPort.screenSanctions(
                command.getEntityName(),
                command.getCountry(),
                command.getDateOfBirth()
        );
        sanctionsHits.forEach(check::addSanctionsHit);

        // Perform PEP screening
        List<ScreeningHit> pepHits = screeningPort.screenPEP(
                command.getEntityName(),
                command.getCountry(),
                command.getPosition()
        );
        pepHits.forEach(check::addPEPHit);

        // Perform AML screening
        List<ScreeningHit> amlHits = screeningPort.screenAML(
                command.getEntityName(),
                command.getCountry(),
                command.getTransactionDetails()
        );
        amlHits.forEach(check::addAMLHit);

        // Analyze results with AI
        ComplianceScreeningPort.ScreeningAnalysisResult aiAnalysis = screeningPort.analyzeWithAI(
                command.getEntityName(),
                sanctionsHits,
                pepHits,
                amlHits
        );

        // Complete screening with AI analysis
        check.completeScreening(
                aiAnalysis.aiConfidenceScore(),
                aiAnalysis.falsePositiveProbability(),
                aiAnalysis.reasoning()
        );

        // Save updated check
        ComplianceCheck updatedCheck = complianceCheckRepository.save(check);

        // Publish domain events
        updatedCheck.getDomainEvents().forEach(eventPublisher::publish);
        updatedCheck.clearDomainEvents();

        log.info("Compliance screening completed: checkId={}, decision={}, totalHits={}",
                checkId, check.getDecision(), check.getTotalHitsCount());
    }
}

