package com.tradesphere.ai.document.application.port.in;

import com.tradesphere.ai.document.domain.model.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Use case for listing documents
 */
public interface ListDocumentsUseCase {

    Page<Document> listDocuments(Pageable pageable);

    Page<Document> listDocumentsByEntity(String entityType, String entityId, Pageable pageable);

    Page<Document> listDocumentsByStatus(String status, Pageable pageable);
}

