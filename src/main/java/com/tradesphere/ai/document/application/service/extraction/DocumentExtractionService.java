package com.tradesphere.ai.document.application.service.extraction;

import com.tradesphere.ai.document.domain.model.DocumentType;

import java.io.InputStream;

/**
 * Interface for document extraction service
 * Abstracts AI extraction logic for testability
 */
public interface DocumentExtractionService {
    
    /**
     * Extract structured data from document
     * High-performance with streaming support
     */
    ExtractionResult extractDocument(
        DocumentType documentType,
        InputStream documentStream,
        String fileName,
        String mimeType
    );
    
    /**
     * Check if document type requires and supports extraction
     */
    boolean supportsExtraction(DocumentType documentType);
}

