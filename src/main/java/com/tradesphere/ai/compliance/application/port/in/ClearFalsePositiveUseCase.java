package com.tradesphere.ai.compliance.application.port.in;

/**
 * Inbound port for clearing false positives
 */
public interface ClearFalsePositiveUseCase {
    
    /**
     * Clear a compliance check hit as false positive
     * 
     * @param command clear false positive command
     */
    void clearAsFalsePositive(ClearFalsePositiveCommand command);
}

