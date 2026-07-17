package com.tradesphere.ai.lc.application.port.in;

import com.tradesphere.ai.lc.domain.model.LetterOfCredit;

/**
 * Use case for creating a Letter of Credit application
 */
public interface CreateLCUseCase {
    
    /**
     * Create a new LC application
     * @param command The LC creation command
     * @return The created LC
     */
    LetterOfCredit createLC(CreateLCCommand command);
}

