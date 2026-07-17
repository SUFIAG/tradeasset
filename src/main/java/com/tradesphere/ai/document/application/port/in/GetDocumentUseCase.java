package com.tradesphere.ai.document.application.port.in;

import com.tradesphere.ai.document.domain.model.Document;

import java.util.Optional;

/**
 * Use case for retrieving documents
 */
public interface GetDocumentUseCase {
    
    Optional<Document> getDocumentById(String documentId);
    
    Optional<Document> getDocumentByStorageKey(String storageKey);
}

