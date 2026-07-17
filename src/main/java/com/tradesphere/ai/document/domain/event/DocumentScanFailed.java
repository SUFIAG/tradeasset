package com.tradesphere.ai.document.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

/**
 * Event raised when document scan fails (virus detected)
 */
@Getter
public class DocumentScanFailed extends BaseDomainEvent {
    
    private final String documentId;
    private final String fileName;
    private final String scanResult;
    
    public DocumentScanFailed(String documentId, String fileName, String scanResult) {
        super(documentId, "Document");
        this.documentId = documentId;
        this.fileName = fileName;
        this.scanResult = scanResult;
    }
}

