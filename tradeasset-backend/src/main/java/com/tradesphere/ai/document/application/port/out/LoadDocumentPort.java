package com.tradesphere.ai.document.application.port.out;

import com.tradesphere.ai.document.domain.model.Document;
import com.tradesphere.ai.document.domain.model.DocumentId;

import java.util.Optional;

/**
 * Port for loading documents from persistence
 */
public interface LoadDocumentPort {

    Optional<Document> loadDocumentById(DocumentId documentId);

    Optional<Document> loadDocumentByStorageKey(String storageKey);
}

