package com.tradesphere.ai.lc.application.port.in;

import com.tradesphere.ai.lc.domain.model.LetterOfCredit;

import java.util.UUID;

/**
 * Use case for waiving document discrepancies
 */
public interface WaiveDiscrepanciesUseCase {
    
    /**
     * Waive discrepancies and proceed with payment
     */
    LetterOfCredit waiveDiscrepancies(String lcId, UUID waivedBy);
}

