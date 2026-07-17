package com.tradesphere.ai.document.application.port.out;

import com.tradesphere.ai.document.domain.model.Document;

/**
 * Port for saving documents to persistence
 */
public interface SaveDocumentPort {
    
    Document saveDocument(Document document);
}

