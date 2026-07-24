package com.tradesphere.ai.lc.application.port.in;

import com.tradesphere.ai.lc.domain.model.LetterOfCredit;

import java.util.Optional;

/**
 * Use case for retrieving LC information
 */
public interface GetLCUseCase {
    
    /**
     * Get LC by ID
     */
    Optional<LetterOfCredit> getLCById(String lcId);
    
    /**
     * Get LC by LC Number
     */
    Optional<LetterOfCredit> getLCByNumber(String lcNumber);
}

