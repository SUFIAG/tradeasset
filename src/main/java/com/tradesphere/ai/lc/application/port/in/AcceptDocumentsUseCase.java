package com.tradesphere.ai.lc.application.port.in;

import com.tradesphere.ai.lc.domain.model.LetterOfCredit;

import java.util.UUID;

/**
 * Use case for accepting LC documents
 */
public interface AcceptDocumentsUseCase {

    /**
     * Accept documents for an LC
     * @param lcId LC identifier
     * @param acceptedBy User who accepted documents
     * @return Updated LC
     */
    LetterOfCredit acceptDocuments(String lcId, UUID acceptedBy);
}

