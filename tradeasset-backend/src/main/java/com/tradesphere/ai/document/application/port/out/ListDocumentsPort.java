package com.tradesphere.ai.document.application.port.out;

import com.tradesphere.ai.document.domain.model.Document;
import com.tradesphere.ai.document.domain.model.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Port for listing documents with filtering
 */
public interface ListDocumentsPort {

    Page<Document> findAll(Pageable pageable);

    Page<Document> findByRelatedEntity(String entityType, String entityId, Pageable pageable);

    Page<Document> findByStatus(DocumentStatus status, Pageable pageable);
}

