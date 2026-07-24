package com.tradesphere.ai.lc.application.port.in;

import com.tradesphere.ai.lc.domain.model.LetterOfCredit;

import java.util.UUID;

/**
 * Use case for receiving documents for LC review
 */
public interface ReceiveDocumentsUseCase {

    /**
     * Mark documents as received for an issued LC
     */
    LetterOfCredit receiveDocuments(String lcId, UUID receivedBy);
}

