package com.tradesphere.ai.document.application.port.in;

import com.tradesphere.ai.document.domain.model.Document;

import java.util.UUID;

/**
 * Use case for verifying documents
 */
public interface VerifyDocumentUseCase {
    
    Document verifyDocument(String documentId, UUID verifiedBy);
    
    Document rejectDocument(String documentId, UUID rejectedBy, String reason);
}

