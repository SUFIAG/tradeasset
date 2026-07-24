package com.tradesphere.ai.risk.application.service;

import com.tradesphere.ai.risk.application.port.in.InitiateRiskAssessmentCommand;
import com.tradesphere.ai.risk.application.port.in.InitiateRiskAssessmentUseCase;
import com.tradesphere.ai.risk.application.port.out.AIRiskScoringPort;
import com.tradesphere.ai.risk.application.port.out.PublishRiskEventPort;
import com.tradesphere.ai.risk.application.port.out.RiskAssessmentRepository;
import com.tradesphere.ai.risk.domain.model.RiskAssessment;
import com.tradesphere.ai.risk.domain.model.RiskAssessmentId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for initiating risk assessments
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InitiateRiskAssessmentService implements InitiateRiskAssessmentUseCase {
    
    private final RiskAssessmentRepository repository;
    private final AIRiskScoringPort aiScoringPort;
    private final PublishRiskEventPort eventPublisher;
    
    @Override
    public RiskAssessmentId initiateAssessment(InitiateRiskAssessmentCommand command) {
        log.info("Initiating risk assessment: type={}, entityType={}, entityId={}",
                command.getRiskType(), command.getEntityType(), command.getEntityId());
        
        // Create risk assessment
        RiskAssessment assessment = RiskAssessment.create(
                command.getTenantId(),
                command.getRiskType(),
                command.getEntityType(),
                command.getEntityId(),
                command.getEntityName(),
                command.getInitiatedBy()
        );
        
        // Save initial assessment
        RiskAssessment savedAssessment = repository.save(assessment);
        
        // Publish domain events
        savedAssessment.getDomainEvents().forEach(eventPublisher::publish);
        savedAssessment.clearDomainEvents();
        
        log.info("Risk assessment initiated: id={}, assessmentNumber={}",
                savedAssessment.getId(), savedAssessment.getAssessmentNumber());
        
        return savedAssessment.getId();
    }
    
    /**
     * Perform the actual risk scoring (called by event listener or directly)
     */
    public void performRiskScoring(RiskAssessmentId assessmentId, InitiateRiskAssessmentCommand command) {
        log.info("Performing risk scoring: assessmentId={}", assessmentId);
        
        // Load assessment
        RiskAssessment assessment = repository
                .findById(assessmentId, command.getTenantId())
                .orElseThrow(() -> new IllegalArgumentException("Risk assessment not found"));
        
        // Calculate country risk if applicable
        if (command.getCountryCode() != null) {
            Integer countryScore = aiScoringPort.calculateCountryRisk(command.getCountryCode());
            assessment.setCountryRisk(command.getCountryCode(), countryScore);
        }
        
        // Calculate credit risk for customer/counterparty
        if ("CUSTOMER".equals(command.getEntityType()) || "COUNTERPARTY".equals(command.getEntityType())) {
            AIRiskScoringPort.RiskScoringResult creditRisk = aiScoringPort.calculateCreditRisk(
                    command.getEntityName(),
                    command.getCountryCode(),
                    command.getTransactionAmount(),
                    command.getCustomerType(),
                    "Historical data placeholder"
            );
            
            assessment.setCreditRisk(creditRisk.creditScore(), creditRisk.reasoning());
            assessment.setPaymentHistoryRisk(creditRisk.paymentHistoryScore());
            assessment.setFinancialHealthRisk(creditRisk.financialHealthScore());
        }
        
        // Calculate transaction risk if applicable
        if (command.getTransactionAmount() != null) {
            Integer transactionScore = aiScoringPort.calculateTransactionRisk(
                    command.getTransactionAmount(),
                    "USD",
                    command.getCountryCode(),
                    "LC"
            );
            assessment.setTransactionRisk(transactionScore);
        }
        
        // Perform AI analysis
        AIRiskScoringPort.RiskAnalysisResult analysis = aiScoringPort.analyzeWithAI(
                command.getRiskType(),
                command.getEntityName(),
                command.getCountryCode(),
                "Additional context"
        );
        
        // Complete assessment
        assessment.completeAssessment(analysis.reasoning(), analysis.recommendation());
        
        // Save updated assessment
        RiskAssessment updatedAssessment = repository.save(assessment);
        
        // Publish domain events
        updatedAssessment.getDomainEvents().forEach(eventPublisher::publish);
        updatedAssessment.clearDomainEvents();
        
        log.info("Risk scoring completed: assessmentId={}, overallScore={}, level={}",
                assessmentId, assessment.getOverallRiskScore(), assessment.getOverallRiskLevel());
    }
}

