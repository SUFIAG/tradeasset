package com.tradesphere.ai.compliance.application.port.in;

/**
 * Inbound port for rejecting compliance checks
 */
public interface RejectComplianceCheckUseCase {
    
    /**
     * Reject a compliance check
     * 
     * @param command rejection command
     */
    void rejectCheck(RejectComplianceCheckCommand command);
}

