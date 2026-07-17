package com.tradesphere.ai.document.application.port.in;

import com.tradesphere.ai.document.domain.model.Document;

import java.util.Map;

/**
 * Use case for triggering document extraction
 */
public interface ExtractDocumentUseCase {

    Document extractDocument(String documentId);

    Document completeExtraction(String documentId, Map<String, Object> extractedData, Float confidence);
}

