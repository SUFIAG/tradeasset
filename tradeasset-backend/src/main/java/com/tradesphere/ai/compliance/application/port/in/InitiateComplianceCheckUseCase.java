package com.tradesphere.ai.compliance.application.port.in;

import com.tradesphere.ai.compliance.domain.model.ComplianceCheckId;

/**
 * Inbound port for initiating compliance checks
 */
public interface InitiateComplianceCheckUseCase {
    
    /**
     * Initiate a new compliance check
     * 
     * @param command compliance check initiation command
     * @return compliance check ID
     */
    ComplianceCheckId initiateCheck(InitiateComplianceCheckCommand command);
}

