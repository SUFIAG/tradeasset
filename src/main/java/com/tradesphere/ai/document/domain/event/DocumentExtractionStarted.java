package com.tradesphere.ai.document.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

/**
 * Event raised when AI extraction starts
 */
@Getter
public class DocumentExtractionStarted extends BaseDomainEvent {
    
    private final String documentId;
    private final String fileName;
    private final String documentType;
    private final String modelName;
    
    public DocumentExtractionStarted(String documentId, String fileName, 
                                    String documentType, String modelName) {
        super(documentId, "Document");
        this.documentId = documentId;
        this.fileName = fileName;
        this.documentType = documentType;
        this.modelName = modelName;
    }
}

