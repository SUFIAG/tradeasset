package com.tradesphere.ai.lc.application.port.in;

import com.tradesphere.ai.lc.domain.model.LetterOfCredit;

import java.util.UUID;

/**
 * Use case for issuing LC
 */
public interface IssueLCUseCase {

    /**
     * Issue an approved LC
     * Generates LC number and SWIFT MT700 message
     */
    LetterOfCredit issueLC(String lcId, UUID issuedBy);
}

