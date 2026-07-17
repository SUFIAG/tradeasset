package com.tradesphere.ai.document.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

/**
 * Event raised when a document is uploaded
 */
@Getter
public class DocumentUploaded extends BaseDomainEvent {

    private final String documentId;
    private final String fileName;
    private final String documentType;
    private final long fileSize;
    private final String relatedEntityType;
    private final String relatedEntityId;

    public DocumentUploaded(String documentId, String fileName, String documentType,
                           long fileSize, String relatedEntityType, String relatedEntityId) {
        super(documentId, "Document");
        this.documentId = documentId;
        this.fileName = fileName;
        this.documentType = documentType;
        this.fileSize = fileSize;
        this.relatedEntityType = relatedEntityType;
        this.relatedEntityId = relatedEntityId;
    }
}

