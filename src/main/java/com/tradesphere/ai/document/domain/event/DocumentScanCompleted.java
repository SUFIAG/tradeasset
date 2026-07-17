package com.tradesphere.ai.document.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

/**
 * Event raised when document scan completes
 */
@Getter
public class DocumentScanCompleted extends BaseDomainEvent {
    
    private final String documentId;
    private final String fileName;
    private final boolean passed;
    private final String scanResult;
    
    public DocumentScanCompleted(String documentId, String fileName, 
                                boolean passed, String scanResult) {
        super(documentId, "Document");
        this.documentId = documentId;
        this.fileName = fileName;
        this.passed = passed;
        this.scanResult = scanResult;
    }
}

