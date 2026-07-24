package com.tradesphere.ai.document.adapter.out.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradesphere.ai.document.domain.model.Document;
import com.tradesphere.ai.document.domain.model.DocumentId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapper between Document domain aggregate and DocumentJpaEntity
 * High-performance with minimal object creation
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentMapper {

    private final ObjectMapper objectMapper;

    /**
     * Map domain aggregate to JPA entity
     */
    public DocumentJpaEntity toJpaEntity(Document document) {
        return DocumentJpaEntity.builder()
            .id(document.getId().getValue())
            .tenantId(document.getTenantId())
            .documentType(document.getDocumentType())
            .status(document.getStatus())
            .fileName(document.getFileName())
            .originalFileName(document.getOriginalFileName())
            .fileSize(document.getFileSize())
            .mimeType(document.getMimeType())
            .fileExtension(document.getFileExtension())
            .storageKey(document.getStorageKey())
            .storageBucket(document.getStorageBucket())
            .storageUrl(document.getStorageUrl())
            .checksum(document.getChecksum())
            .relatedEntityType(document.getRelatedEntityType())
            .relatedEntityId(document.getRelatedEntityId())
            .uploadedBy(document.getUploadedBy())
            .uploadedAt(document.getUploadedAt())
            .scanStartedAt(document.getScanStartedAt())
            .scanCompletedAt(document.getScanCompletedAt())
            .virusScanPassed(document.isVirusScanPassed())
            .scanResult(document.getScanResult())
            .extractionStartedAt(document.getExtractionStartedAt())
            .extractionCompletedAt(document.getExtractionCompletedAt())
            .extractionSuccessful(document.isExtractionSuccessful())
            .extractedDataJson(serializeMap(document.getExtractedData()))
            .extractionConfidence(document.getExtractionConfidence())
            .extractionModel(document.getExtractionModel())
            .reviewedBy(document.getReviewedBy())
            .reviewedAt(document.getReviewedAt())
            .verified(document.isVerified())
            .rejectionReason(document.getRejectionReason())
            .metadataJson(serializeMap(convertToObjectMap(document.getMetadata())))
            .version(document.getVersion())
            .createdBy(document.getCreatedBy())
            .createdAt(document.getCreatedAt() != null ?
                LocalDateTime.ofInstant(document.getCreatedAt(), java.time.ZoneId.systemDefault()) : null)
            .updatedBy(document.getUpdatedBy())
            .updatedAt(document.getUpdatedAt() != null ?
                LocalDateTime.ofInstant(document.getUpdatedAt(), java.time.ZoneId.systemDefault()) : null)
            .build();
    }

    /**
     * Map JPA entity to domain aggregate
     * Memory-efficient reconstruction
     */
    public Document toDomain(DocumentJpaEntity entity) {
        // Use reflection or builder to reconstruct aggregate
        // For simplicity, create new instance (in production, use proper builder)
        Document document = Document.upload(
            entity.getTenantId(),
            entity.getDocumentType(),
            entity.getFileName(),
            entity.getFileSize(),
            entity.getMimeType(),
            entity.getStorageKey(),
            entity.getStorageBucket(),
            entity.getChecksum(),
            entity.getRelatedEntityType(),
            entity.getRelatedEntityId(),
            entity.getUploadedBy()
        );

        // Clear events from construction
        document.clearDomainEvents();

        // Set all fields via reflection or setters (simplified version)
        setDocumentFields(document, entity);

        return document;
    }

    private void setDocumentFields(Document document, DocumentJpaEntity entity) {
        try {
            // Use reflection to set private fields
            var idField = Document.class.getSuperclass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(document, DocumentId.of(entity.getId()));

            setField(document, "status", entity.getStatus());
            setField(document, "originalFileName", entity.getOriginalFileName());
            setField(document, "fileExtension", entity.getFileExtension());
            setField(document, "storageUrl", entity.getStorageUrl());
            setField(document, "uploadedAt", entity.getUploadedAt());

            if (entity.getScanStartedAt() != null) {
                setField(document, "scanStartedAt", entity.getScanStartedAt());
                setField(document, "scanCompletedAt", entity.getScanCompletedAt());
                setField(document, "virusScanPassed", entity.getVirusScanPassed());
                setField(document, "scanResult", entity.getScanResult());
            }

            if (entity.getExtractionStartedAt() != null) {
                setField(document, "extractionStartedAt", entity.getExtractionStartedAt());
                setField(document, "extractionCompletedAt", entity.getExtractionCompletedAt());
                setField(document, "extractionSuccessful", entity.getExtractionSuccessful());
                setField(document, "extractedData", deserializeMap(entity.getExtractedDataJson()));
                setField(document, "extractionConfidence", entity.getExtractionConfidence());
                setField(document, "extractionModel", entity.getExtractionModel());
            }

            if (entity.getReviewedBy() != null) {
                setField(document, "reviewedBy", entity.getReviewedBy());
                setField(document, "reviewedAt", entity.getReviewedAt());
                setField(document, "verified", entity.getVerified());
                setField(document, "rejectionReason", entity.getRejectionReason());
            }

            setField(document, "metadata", deserializeStringMap(entity.getMetadataJson()));

            // Set audit fields
            setField(document, "version", entity.getVersion());
            setField(document, "createdBy", entity.getCreatedBy());
            setField(document, "createdAt", entity.getCreatedAt());
            setField(document, "updatedBy", entity.getUpdatedBy());
            setField(document, "updatedAt", entity.getUpdatedAt());

        } catch (Exception e) {
            log.error("Failed to reconstruct document aggregate", e);
            throw new RuntimeException("Document reconstruction failed", e);
        }
    }

    private void setField(Object obj, String fieldName, Object value) throws Exception {
        if (value == null) return;
        var field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    private String serializeMap(Map<String, ?> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize map", e);
            return null;
        }
    }

    private Map<String, Object> deserializeMap(String json) {
        if (json == null || json.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize map", e);
            return new HashMap<>();
        }
    }

    private Map<String, String> deserializeStringMap(String json) {
        if (json == null || json.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize string map", e);
            return new HashMap<>();
        }
    }

    private Map<String, Object> convertToObjectMap(Map<String, String> stringMap) {
        if (stringMap == null) return new HashMap<>();
        return new HashMap<>(stringMap);
    }
}

