package com.tradesphere.ai.document.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

/**
 * Event raised when AI extraction fails
 */
@Getter
public class DocumentExtractionFailed extends BaseDomainEvent {

    private final String documentId;
    private final String fileName;
    private final String errorMessage;

    public DocumentExtractionFailed(String documentId, String fileName, String errorMessage) {
        super(documentId, "Document");
        this.documentId = documentId;
        this.fileName = fileName;
        this.errorMessage = errorMessage;
    }
}

