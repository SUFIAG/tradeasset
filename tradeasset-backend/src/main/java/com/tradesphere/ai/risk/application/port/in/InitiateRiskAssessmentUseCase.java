package com.tradesphere.ai.risk.application.port.in;

import com.tradesphere.ai.risk.domain.model.RiskAssessmentId;

/**
 * Inbound port for initiating risk assessments
 */
public interface InitiateRiskAssessmentUseCase {
    
    /**
     * Initiate a new risk assessment
     */
    RiskAssessmentId initiateAssessment(InitiateRiskAssessmentCommand command);
}

