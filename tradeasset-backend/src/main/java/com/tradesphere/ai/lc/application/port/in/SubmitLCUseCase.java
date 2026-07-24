package com.tradesphere.ai.lc.application.port.in;

import com.tradesphere.ai.lc.domain.model.LetterOfCredit;

import java.util.UUID;

/**
 * Use case for submitting LC for approval
 */
public interface SubmitLCUseCase {
    
    /**
     * Submit LC for approval
     */
    LetterOfCredit submitForApproval(String lcId, UUID submittedBy);
}

