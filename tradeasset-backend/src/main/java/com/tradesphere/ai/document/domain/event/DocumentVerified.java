package com.tradesphere.ai.document.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

/**
 * Event raised when document is verified
 */
@Getter
public class DocumentVerified extends BaseDomainEvent {
    
    private final String documentId;
    private final String fileName;
    private final String verifiedBy;
    
    public DocumentVerified(String documentId, String fileName, String verifiedBy) {
        super(documentId, "Document");
        this.documentId = documentId;
        this.fileName = fileName;
        this.verifiedBy = verifiedBy;
    }
}

