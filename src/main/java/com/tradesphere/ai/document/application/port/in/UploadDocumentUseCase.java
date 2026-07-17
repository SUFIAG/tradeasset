package com.tradesphere.ai.document.application.port.in;

import com.tradesphere.ai.document.domain.model.Document;
import lombok.Builder;
import lombok.Value;

import java.io.InputStream;
import java.util.UUID;

/**
 * Use case for uploading documents
 */
public interface UploadDocumentUseCase {

    Document uploadDocument(UploadDocumentCommand command);

    @Value
    @Builder
    class UploadDocumentCommand {
        UUID tenantId;
        String documentType;
        String fileName;
        long fileSize;
        String mimeType;
        InputStream fileStream;
        String relatedEntityType;
        String relatedEntityId;
        UUID uploadedBy;
    }
}

