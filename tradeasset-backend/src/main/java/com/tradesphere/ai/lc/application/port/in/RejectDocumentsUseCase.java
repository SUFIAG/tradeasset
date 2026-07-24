package com.tradesphere.ai.lc.application.port.in;

import com.tradesphere.ai.lc.domain.model.LetterOfCredit;

import java.util.UUID;

/**
 * Use case for rejecting LC documents
 */
public interface RejectDocumentsUseCase {
    
    /**
     * Reject documents for an LC due to discrepancies
     * @param lcId LC identifier
     * @param discrepancies Description of discrepancies found
     * @param rejectedBy User who rejected documents
     * @return Updated LC
     */
    LetterOfCredit rejectDocuments(String lcId, String discrepancies, UUID rejectedBy);
}

