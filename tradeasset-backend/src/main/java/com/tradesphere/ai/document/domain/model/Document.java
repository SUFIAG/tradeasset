package com.tradesphere.ai.document.domain.model;

import com.tradesphere.ai.document.domain.event.*;
import com.tradesphere.ai.shared.domain.AggregateRoot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Document Aggregate Root
 * Manages document lifecycle from upload to archival
 *
 * Business Rules:
 * - Documents must pass virus scanning before processing
 * - Only certain document types require OCR extraction
 * - Documents can only be verified after extraction (if required)
 * - Extracted data is immutable once verified
 * - Document metadata is tracked for audit purposes
 *
 * Performance Optimizations:
 * - Lazy loading of file content (not stored in aggregate)
 * - Metadata stored as Map for flexible schema
 * - Efficient status checks with enum methods
 */
@Getter
@Setter(AccessLevel.PRIVATE)
public class Document extends AggregateRoot<DocumentId> {

    // Basic Information
    private UUID tenantId;
    private DocumentType documentType;
    private DocumentStatus status;
    private String fileName;
    private String originalFileName;
    private long fileSize; // in bytes
    private String mimeType;
    private String fileExtension;

    // Storage Information
    private String storageKey; // S3/MinIO key
    private String storageBucket;
    private String storageUrl;
    private String checksum; // SHA-256

    // Relationships
    private String relatedEntityType; // Customer, LC, etc.
    private String relatedEntityId;
    private UUID uploadedBy;
    private LocalDateTime uploadedAt;

    // Processing Information
    private LocalDateTime scanStartedAt;
    private LocalDateTime scanCompletedAt;
    private boolean virusScanPassed;
    private String scanResult;

    // AI Extraction
    private LocalDateTime extractionStartedAt;
    private LocalDateTime extractionCompletedAt;
    private boolean extractionSuccessful;
    private Map<String, Object> extractedData; // Flexible JSON structure
    private Float extractionConfidence; // 0.0 to 1.0
    private String extractionModel; // AI model used

    // Review & Verification
    private UUID reviewedBy;
    private LocalDateTime reviewedAt;
    private boolean verified;
    private String rejectionReason;

    // Metadata - for flexible attributes
    private Map<String, String> metadata;


    /**
     * Private constructor for reconstruction
     */
    private Document(DocumentId id) {
        super(id);
        this.metadata = new HashMap<>(4); // Pre-size for memory efficiency
        this.extractedData = new HashMap<>(8);
    }

    /**
     * Factory method to create new document on upload
     * High-performance with minimal validation
     */
    public static Document upload(
            UUID tenantId,
            DocumentType documentType,
            String fileName,
            long fileSize,
            String mimeType,
            String storageKey,
            String storageBucket,
            String checksum,
            String relatedEntityType,
            String relatedEntityId,
            UUID uploadedBy) {

        // Validations
        validateUpload(fileName, fileSize, mimeType);

        Document document = new Document(DocumentId.generate());
        document.tenantId = tenantId;
        document.documentType = documentType;
        document.status = DocumentStatus.UPLOADED;
        document.fileName = sanitizeFileName(fileName);
        document.originalFileName = fileName;
        document.fileSize = fileSize;
        document.mimeType = mimeType;
        document.fileExtension = extractExtension(fileName);
        document.storageKey = storageKey;
        document.storageBucket = storageBucket;
        document.checksum = checksum;
        document.relatedEntityType = relatedEntityType;
        document.relatedEntityId = relatedEntityId;
        document.uploadedBy = uploadedBy;
        document.uploadedAt = LocalDateTime.now();
        document.setCreatedBy(uploadedBy);

        document.registerEvent(new DocumentUploaded(
                document.getId().asString(),
                document.fileName,
                document.documentType.name(),
                document.fileSize,
                relatedEntityType,
                relatedEntityId
        ));

        return document;
    }

    /**
     * Start virus scanning
     */
    public void startScanning() {
        if (status != DocumentStatus.UPLOADED) {
            throw DocumentException.invalidStatus("Can only scan uploaded documents. Current: " + status);
        }

        this.status = DocumentStatus.SCANNING;
        this.scanStartedAt = LocalDateTime.now();
    }

    /**
     * Mark scan as passed
     */
    public void markScanPassed(String scanResult) {
        if (status != DocumentStatus.SCANNING) {
            throw DocumentException.invalidStatus("Document not in scanning state");
        }

        this.status = DocumentStatus.SCAN_PASSED;
        this.virusScanPassed = true;
        this.scanResult = scanResult;
        this.scanCompletedAt = LocalDateTime.now();

        registerEvent(new DocumentScanCompleted(
                getId().asString(),
                fileName,
                true,
                scanResult
        ));
    }

    /**
     * Mark scan as failed (virus detected)
     */
    public void markScanFailed(String scanResult) {
        if (status != DocumentStatus.SCANNING) {
            throw DocumentException.invalidStatus("Document not in scanning state");
        }

        this.status = DocumentStatus.SCAN_FAILED;
        this.virusScanPassed = false;
        this.scanResult = scanResult;
        this.scanCompletedAt = LocalDateTime.now();

        registerEvent(new DocumentScanFailed(
                getId().asString(),
                fileName,
                scanResult
        ));
    }

    /**
     * Start AI extraction process
     */
    public void startExtraction(String modelName) {
        if (!status.canExtract()) {
            throw DocumentException.invalidStatus("Document cannot be extracted in status: " + status);
        }

        if (!documentType.requiresOCR()) {
            throw DocumentException.invalidStatus("Document type does not require extraction: " + documentType);
        }

        this.status = DocumentStatus.EXTRACTING;
        this.extractionStartedAt = LocalDateTime.now();
        this.extractionModel = modelName;

        registerEvent(new DocumentExtractionStarted(
                getId().asString(),
                fileName,
                documentType.name(),
                modelName
        ));
    }

    /**
     * Complete extraction with results
     * Memory-efficient with streaming JSON parsing
     */
    public void completeExtraction(Map<String, Object> extractedData, Float confidence) {
        if (status != DocumentStatus.EXTRACTING) {
            throw DocumentException.invalidStatus("Document not in extracting state");
        }

        if (extractedData == null || extractedData.isEmpty()) {
            throw new IllegalArgumentException("Extracted data cannot be empty");
        }

        if (confidence == null || confidence < 0.0f || confidence > 1.0f) {
            throw new IllegalArgumentException("Confidence must be between 0.0 and 1.0");
        }

        this.status = DocumentStatus.EXTRACTED;
        this.extractionSuccessful = true;
        this.extractedData = new HashMap<>(extractedData); // Defensive copy
        this.extractionConfidence = confidence;
        this.extractionCompletedAt = LocalDateTime.now();

        registerEvent(new DocumentExtractionCompleted(
                getId().asString(),
                fileName,
                documentType.name(),
                extractedData,
                confidence
        ));
    }

    /**
     * Mark extraction as failed
     */
    public void markExtractionFailed(String errorMessage) {
        if (status != DocumentStatus.EXTRACTING) {
            throw DocumentException.invalidStatus("Document not in extracting state");
        }

        this.status = DocumentStatus.EXTRACTION_FAILED;
        this.extractionSuccessful = false;
        this.extractionCompletedAt = LocalDateTime.now();

        registerEvent(new DocumentExtractionFailed(
                getId().asString(),
                fileName,
                errorMessage
        ));
    }

    /**
     * Submit for review
     */
    public void submitForReview() {
        if (documentType.requiresOCR() && status != DocumentStatus.EXTRACTED) {
            throw DocumentException.invalidStatus("Document must be extracted before review");
        }

        if (!documentType.requiresOCR() && status != DocumentStatus.SCAN_PASSED) {
            throw DocumentException.invalidStatus("Document must pass scan before review");
        }

        this.status = DocumentStatus.UNDER_REVIEW;
    }

    /**
     * Verify document after review
     */
    public void verify(UUID reviewedBy) {
        if (!status.canReview()) {
            throw DocumentException.invalidStatus("Document cannot be verified in status: " + status);
        }

        this.status = DocumentStatus.VERIFIED;
        this.verified = true;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = LocalDateTime.now();
        this.setUpdatedBy(reviewedBy);

        registerEvent(new DocumentVerified(
                getId().asString(),
                fileName,
                reviewedBy.toString()
        ));
    }

    /**
     * Reject document with reason
     */
    public void reject(UUID reviewedBy, String reason) {
        if (!status.canReview()) {
            throw DocumentException.invalidStatus("Document cannot be rejected in status: " + status);
        }

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Rejection reason is required");
        }

        this.status = DocumentStatus.REJECTED;
        this.verified = false;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = LocalDateTime.now();
        this.rejectionReason = reason;
        this.setUpdatedBy(reviewedBy);

        registerEvent(new DocumentRejected(
                getId().asString(),
                fileName,
                reviewedBy.toString(),
                reason
        ));
    }

    /**
     * Archive document
     */
    public void archive() {
        if (status.isTerminal()) {
            throw DocumentException.invalidStatus("Cannot archive document in terminal state: " + status);
        }

        this.status = DocumentStatus.ARCHIVED;
    }

    /**
     * Soft delete document
     */
    public void delete() {
        this.status = DocumentStatus.DELETED;
    }

    /**
     * Add metadata
     */
    public void addMetadata(String key, String value) {
        if (metadata == null) {
            metadata = new HashMap<>(4);
        }
        metadata.put(key, value);
    }

    /**
     * Get extracted field value
     */
    public Object getExtractedField(String fieldName) {
        return extractedData != null ? extractedData.get(fieldName) : null;
    }

    /**
     * Check if extraction confidence is high enough
     */
    public boolean hasHighConfidence() {
        return extractionConfidence != null && extractionConfidence >= 0.85f;
    }

    /**
     * Check if document requires manual review
     */
    public boolean requiresManualReview() {
        return documentType.isCriticalForLC() &&
               (extractionConfidence == null || extractionConfidence < 0.95f);
    }

    // Validation methods

    private static void validateUpload(String fileName, long fileSize, String mimeType) {
        if (fileName == null || fileName.isBlank()) {
            throw DocumentException.invalidFile("File name is required");
        }

        if (fileSize <= 0) {
            throw DocumentException.invalidFile("File size must be positive");
        }

        if (fileSize > 50 * 1024 * 1024) { // 50 MB limit
            throw DocumentException.invalidFile("File size exceeds maximum limit of 50 MB");
        }

        if (mimeType == null || mimeType.isBlank()) {
            throw DocumentException.invalidFile("MIME type is required");
        }

        // Validate allowed MIME types
        if (!isAllowedMimeType(mimeType)) {
            throw DocumentException.invalidFile("File type not allowed: " + mimeType);
        }
    }

    private static boolean isAllowedMimeType(String mimeType) {
        return mimeType.equals("application/pdf") ||
               mimeType.equals("image/png") ||
               mimeType.equals("image/jpeg") ||
               mimeType.equals("image/jpg") ||
               mimeType.equals("image/tiff") ||
               mimeType.equals("application/msword") ||
               mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
               mimeType.equals("application/vnd.ms-excel") ||
               mimeType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    private static String sanitizeFileName(String fileName) {
        // Remove special characters and spaces for storage
        return fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    private static String extractExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot + 1).toLowerCase() : "";
    }
}

