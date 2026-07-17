package com.tradesphere.ai.lc.application.port.in;

import com.tradesphere.ai.lc.domain.model.LetterOfCredit;

import java.util.UUID;

/**
 * Use case for rejecting LC application
 */
public interface RejectLCUseCase {
    
    /**
     * Reject LC application
     */
    LetterOfCredit rejectLC(String lcId, String reason, UUID rejectedBy);
}

