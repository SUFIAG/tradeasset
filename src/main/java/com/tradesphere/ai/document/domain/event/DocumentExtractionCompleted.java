package com.tradesphere.ai.document.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.util.Map;

/**
 * Event raised when AI extraction completes successfully
 */
@Getter
public class DocumentExtractionCompleted extends BaseDomainEvent {
    
    private final String documentId;
    private final String fileName;
    private final String documentType;
    private final Map<String, Object> extractedData;
    private final Float confidence;
    
    public DocumentExtractionCompleted(String documentId, String fileName, 
                                      String documentType, Map<String, Object> extractedData,
                                      Float confidence) {
        super(documentId, "Document");
        this.documentId = documentId;
        this.fileName = fileName;
        this.documentType = documentType;
        this.extractedData = extractedData;
        this.confidence = confidence;
    }
}

