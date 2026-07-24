package com.tradesphere.ai.document.domain.model;

import com.tradesphere.ai.shared.domain.DomainException;

/**
 * Domain exception for Document operations
 */
public class DocumentException extends DomainException {

    public DocumentException(String message) {
        super("DOCUMENT_ERROR", message);
    }

    public DocumentException(String code, String message) {
        super(code, message);
    }

    public static DocumentException invalidStatus(String message) {
        return new DocumentException("INVALID_STATUS", message);
    }

    public static DocumentException invalidFile(String message) {
        return new DocumentException("INVALID_FILE", message);
    }

    public static DocumentException storageError(String message) {
        return new DocumentException("STORAGE_ERROR", message);
    }

    public static DocumentException virusDetected(String message) {
        return new DocumentException("VIRUS_DETECTED", message);
    }

    public static DocumentException extractionFailed(String message) {
        return new DocumentException("EXTRACTION_FAILED", message);
    }

    public static DocumentException notFound(String message) {
        return new DocumentException("NOT_FOUND", message);
    }
}

