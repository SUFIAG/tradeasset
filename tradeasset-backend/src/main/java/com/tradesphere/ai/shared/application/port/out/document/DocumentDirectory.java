package com.tradesphere.ai.shared.application.port.out.document;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Cross-module view of stored documents (REQ-DOC-001, REQ-DOC-007). Implemented by the document
 * module; used for example by KYC to check that required documents are verified, and by the LC
 * module to link presented documents.
 */
public interface DocumentDirectory {

    /** Latest version of every document attached to the record. */
    List<DocumentSummary> listFor(UUID tenantId, String entityType, UUID entityId);

    Optional<DocumentSummary> findById(UUID tenantId, UUID documentId);

    /**
     * @param documentType stable code, for example TRADE_LICENSE, COMMERCIAL_INVOICE, BILL_OF_LADING
     * @param status       PENDING, VERIFIED or REJECTED (REQ-DOC-007)
     */
    record DocumentSummary(UUID id, String entityType, UUID entityId, String documentType, String fileName,
                           int version, String status, String scanStatus, Instant uploadedAt, UUID uploadedBy) {

        public boolean isVerified() {
            return "VERIFIED".equals(status);
        }
    }
}
