package com.tradesphere.ai.document.adapter.out.persistence;

import com.tradesphere.ai.document.domain.model.DocumentStatus;
import com.tradesphere.ai.document.domain.model.DocumentType;
import com.tradesphere.ai.shared.multitenancy.TenantEntityListener;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * JPA Entity for Document
 * Optimized for high-performance queries with proper indexing
 */
@Entity
@Table(name = "documents", indexes = {
    @Index(name = "idx_document_tenant", columnList = "tenant_id"),
    @Index(name = "idx_document_status", columnList = "status"),
    @Index(name = "idx_document_storage_key", columnList = "storage_key"),
    @Index(name = "idx_document_related_entity", columnList = "related_entity_type,related_entity_id"),
    @Index(name = "idx_document_uploaded_at", columnList = "uploaded_at")
})
@EntityListeners(TenantEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentJpaEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 50)
    private DocumentType documentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private DocumentStatus status;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "original_file_name", length = 255)
    private String originalFileName;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "file_extension", length = 10)
    private String fileExtension;

    // Storage Information
    @Column(name = "storage_key", unique = true, length = 500)
    private String storageKey;

    @Column(name = "storage_bucket", length = 100)
    private String storageBucket;

    @Column(name = "storage_url", length = 1000)
    private String storageUrl;

    @Column(name = "checksum", length = 64)
    private String checksum;

    // Relationships
    @Column(name = "related_entity_type", length = 50)
    private String relatedEntityType;

    @Column(name = "related_entity_id", length = 100)
    private String relatedEntityId;

    @Column(name = "uploaded_by")
    private UUID uploadedBy;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    // Processing Information
    @Column(name = "scan_started_at")
    private LocalDateTime scanStartedAt;

    @Column(name = "scan_completed_at")
    private LocalDateTime scanCompletedAt;

    @Column(name = "virus_scan_passed")
    private Boolean virusScanPassed;

    @Column(name = "scan_result", length = 500)
    private String scanResult;

    // AI Extraction
    @Column(name = "extraction_started_at")
    private LocalDateTime extractionStartedAt;

    @Column(name = "extraction_completed_at")
    private LocalDateTime extractionCompletedAt;

    @Column(name = "extraction_successful")
    private Boolean extractionSuccessful;

    @Column(name = "extracted_data", columnDefinition = "TEXT")
    private String extractedDataJson; // Stored as JSON string

    @Column(name = "extraction_confidence")
    private Float extractionConfidence;

    @Column(name = "extraction_model", length = 50)
    private String extractionModel;

    // Review & Verification
    @Column(name = "reviewed_by")
    private UUID reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "verified")
    private Boolean verified;

    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;

    // Metadata as JSON
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadataJson;

    // Audit fields
    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

