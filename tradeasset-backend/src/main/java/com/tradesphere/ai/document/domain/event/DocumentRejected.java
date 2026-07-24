package com.tradesphere.ai.document.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

/**
 * Event raised when document is rejected
 */
@Getter
public class DocumentRejected extends BaseDomainEvent {

    private final String documentId;
    private final String fileName;
    private final String rejectedBy;
    private final String reason;

    public DocumentRejected(String documentId, String fileName,
                           String rejectedBy, String reason) {
        super(documentId, "Document");
        this.documentId = documentId;
        this.fileName = fileName;
        this.rejectedBy = rejectedBy;
        this.reason = reason;
    }
}

