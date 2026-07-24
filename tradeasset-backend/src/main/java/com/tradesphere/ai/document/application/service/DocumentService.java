package com.tradesphere.ai.document.application.service;

import com.tradesphere.ai.document.application.port.in.*;
import com.tradesphere.ai.document.application.port.out.*;
import com.tradesphere.ai.document.domain.model.Document;
import com.tradesphere.ai.document.domain.model.DocumentId;
import com.tradesphere.ai.document.domain.model.DocumentStatus;
import com.tradesphere.ai.document.domain.model.DocumentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Document Service - Orchestrates document operations
 * High-performance implementation with streaming and async processing
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentService implements
        UploadDocumentUseCase,
        GetDocumentUseCase,
        ListDocumentsUseCase,
        VerifyDocumentUseCase,
        DownloadDocumentUseCase,
        ExtractDocumentUseCase {

    private final LoadDocumentPort loadDocumentPort;
    private final SaveDocumentPort saveDocumentPort;
    private final ListDocumentsPort listDocumentsPort;
    private final FileStoragePort fileStoragePort;
    private final VirusScanPort virusScanPort;
    private final PublishDocumentEventPort publishEventPort;

    private static final String DEFAULT_BUCKET = "documents";

    @Override
    public Document uploadDocument(UploadDocumentCommand command) {
        log.info("Uploading document: {} for entity: {}/{}",
            command.getFileName(), command.getRelatedEntityType(), command.getRelatedEntityId());

        try {
            // Generate storage key
            String storageKey = generateStorageKey(command.getTenantId(), command.getFileName());

            // Store file (streaming for memory efficiency)
            String storedKey = fileStoragePort.storeFile(
                DEFAULT_BUCKET,
                storageKey,
                command.getFileStream(),
                command.getFileSize(),
                command.getMimeType()
            );

            // Calculate checksum (simplified - in production, do during upload)
            String checksum = generateChecksum(command.getFileName());

            // Create domain aggregate
            Document document = Document.upload(
                command.getTenantId(),
                DocumentType.valueOf(command.getDocumentType()),
                command.getFileName(),
                command.getFileSize(),
                command.getMimeType(),
                storedKey,
                DEFAULT_BUCKET,
                checksum,
                command.getRelatedEntityType(),
                command.getRelatedEntityId(),
                command.getUploadedBy()
            );

            // Save to database
            Document savedDocument = saveDocumentPort.saveDocument(document);

            // Publish events
            savedDocument.getDomainEvents().forEach(publishEventPort::publish);
            savedDocument.clearDomainEvents();

            // Trigger async virus scan (would be done via event listener in production)
            triggerVirusScan(savedDocument.getId().asString());

            log.info("Document uploaded successfully: {}", savedDocument.getId());
            return savedDocument;

        } catch (Exception e) {
            log.error("Failed to upload document: {}", command.getFileName(), e);
            throw new RuntimeException("Document upload failed", e);
        }
    }

    @Override
    public Optional<Document> getDocumentById(String documentId) {
        log.debug("Retrieving document: {}", documentId);
        return loadDocumentPort.loadDocumentById(DocumentId.of(documentId));
    }

    @Override
    public Optional<Document> getDocumentByStorageKey(String storageKey) {
        log.debug("Retrieving document by storage key: {}", storageKey);
        return loadDocumentPort.loadDocumentByStorageKey(storageKey);
    }

    @Override
    public Page<Document> listDocuments(Pageable pageable) {
        log.debug("Listing documents with pagination: {}", pageable);
        return listDocumentsPort.findAll(pageable);
    }

    @Override
    public Page<Document> listDocumentsByEntity(String entityType, String entityId, Pageable pageable) {
        log.debug("Listing documents for entity: {}/{}", entityType, entityId);
        return listDocumentsPort.findByRelatedEntity(entityType, entityId, pageable);
    }

    @Override
    public Page<Document> listDocumentsByStatus(String status, Pageable pageable) {
        log.debug("Listing documents by status: {}", status);
        DocumentStatus documentStatus = DocumentStatus.valueOf(status);
        return listDocumentsPort.findByStatus(documentStatus, pageable);
    }

    @Override
    public Document verifyDocument(String documentId, UUID verifiedBy) {
        log.info("Verifying document: {}", documentId);

        Document document = loadDocumentPort.loadDocumentById(DocumentId.of(documentId))
            .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));

        document.verify(verifiedBy);

        Document savedDocument = saveDocumentPort.saveDocument(document);

        // Publish events
        savedDocument.getDomainEvents().forEach(publishEventPort::publish);
        savedDocument.clearDomainEvents();

        log.info("Document verified: {}", documentId);
        return savedDocument;
    }

    @Override
    public Document rejectDocument(String documentId, UUID rejectedBy, String reason) {
        log.info("Rejecting document: {} with reason: {}", documentId, reason);

        Document document = loadDocumentPort.loadDocumentById(DocumentId.of(documentId))
            .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));

        document.reject(rejectedBy, reason);

        Document savedDocument = saveDocumentPort.saveDocument(document);

        // Publish events
        savedDocument.getDomainEvents().forEach(publishEventPort::publish);
        savedDocument.clearDomainEvents();

        log.info("Document rejected: {}", documentId);
        return savedDocument;
    }

    @Override
    @Transactional(readOnly = true)
    public InputStream downloadDocument(String documentId) {
        log.info("Downloading document: {}", documentId);

        Document document = loadDocumentPort.loadDocumentById(DocumentId.of(documentId))
            .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));

        // Stream file from storage (memory-efficient)
        return fileStoragePort.retrieveFile(document.getStorageBucket(), document.getStorageKey());
    }

    @Override
    public Document extractDocument(String documentId) {
        log.info("Starting extraction for document: {}", documentId);

        Document document = loadDocumentPort.loadDocumentById(DocumentId.of(documentId))
            .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));

        document.startExtraction("gpt-4-vision");

        Document savedDocument = saveDocumentPort.saveDocument(document);

        // Publish events
        savedDocument.getDomainEvents().forEach(publishEventPort::publish);
        savedDocument.clearDomainEvents();

        // Trigger async AI extraction (would be done via event listener)
        log.info("Extraction started for document: {}", documentId);
        return savedDocument;
    }

    @Override
    public Document completeExtraction(String documentId, Map<String, Object> extractedData, Float confidence) {
        log.info("Completing extraction for document: {} with confidence: {}", documentId, confidence);

        Document document = loadDocumentPort.loadDocumentById(DocumentId.of(documentId))
            .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));

        document.completeExtraction(extractedData, confidence);

        Document savedDocument = saveDocumentPort.saveDocument(document);

        // Publish events
        savedDocument.getDomainEvents().forEach(publishEventPort::publish);
        savedDocument.clearDomainEvents();

        log.info("Extraction completed for document: {}", documentId);
        return savedDocument;
    }

    // Helper methods

    /**
     * Trigger virus scan asynchronously
     * In production, this would be handled by event listeners
     */
    private void triggerVirusScan(String documentId) {
        // This would trigger async processing via Kafka event
        log.debug("Virus scan triggered for document: {}", documentId);

        // For now, immediately scan (in production, use async queue)
        performVirusScan(documentId);
    }

    /**
     * Perform actual virus scan
     * Called asynchronously in production
     */
    public void performVirusScan(String documentId) {
        try {
            Document document = loadDocumentPort.loadDocumentById(DocumentId.of(documentId))
                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));

            document.startScanning();
            saveDocumentPort.saveDocument(document);

            // Scan file
            VirusScanPort.ScanResult scanResult = virusScanPort.scanFile(document.getStorageKey());

            if (scanResult.passed()) {
                document.markScanPassed(scanResult.message());
                log.info("Virus scan passed for document: {}", documentId);
            } else {
                document.markScanFailed(scanResult.message());
                log.warn("Virus scan failed for document: {}", documentId);
            }

            Document savedDocument = saveDocumentPort.saveDocument(document);

            // Publish events
            savedDocument.getDomainEvents().forEach(publishEventPort::publish);
            savedDocument.clearDomainEvents();

        } catch (Exception e) {
            log.error("Virus scan error for document: {}", documentId, e);
        }
    }

    /**
     * Generate storage key with tenant isolation
     * Format: tenantId/year/month/uuid-filename
     */
    private String generateStorageKey(UUID tenantId, String fileName) {
        String timestamp = java.time.LocalDate.now().toString().replace("-", "/");
        String uuid = UUID.randomUUID().toString();
        String sanitized = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
        return String.format("%s/%s/%s-%s", tenantId, timestamp, uuid, sanitized);
    }

    /**
     * Generate checksum (SHA-256)
     * In production, calculate during upload streaming
     */
    private String generateChecksum(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            log.error("Checksum generation failed", e);
            return "no-checksum";
        }
    }
}

