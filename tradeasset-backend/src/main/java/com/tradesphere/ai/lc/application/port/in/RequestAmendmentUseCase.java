package com.tradesphere.ai.lc.application.port.in;

import com.tradesphere.ai.lc.domain.model.LetterOfCredit;

import java.util.UUID;

/**
 * Use case for requesting LC amendment
 */
public interface RequestAmendmentUseCase {

    /**
     * Request amendment to an issued LC
     * @param lcId LC identifier
     * @param amendmentDetails Details of requested amendment
     * @param requestedBy User requesting amendment
     * @return Updated LC
     */
    LetterOfCredit requestAmendment(String lcId, String amendmentDetails, UUID requestedBy);
}

