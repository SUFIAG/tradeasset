package com.tradesphere.ai.compliance.application.port.in;

/**
 * Inbound port for approving compliance checks
 */
public interface ApproveComplianceCheckUseCase {
    
    /**
     * Approve a compliance check
     * 
     * @param command approval command
     */
    void approveCheck(ApproveComplianceCheckCommand command);
}

