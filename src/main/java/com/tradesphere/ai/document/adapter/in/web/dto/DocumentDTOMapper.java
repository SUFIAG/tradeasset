package com.tradesphere.ai.document.adapter.in.web.dto;

import com.tradesphere.ai.document.domain.model.Document;
import org.springframework.stereotype.Component;

/**
 * Mapper for Document DTOs
 */
@Component
public class DocumentDTOMapper {

    public DocumentResponse toResponse(Document document) {
        return DocumentResponse.builder()
            .id(document.getId().asString())
            .documentType(document.getDocumentType().name())
            .status(document.getStatus().name())
            .fileName(document.getFileName())
            .originalFileName(document.getOriginalFileName())
            .fileSize(document.getFileSize())
            .mimeType(document.getMimeType())
            .fileExtension(document.getFileExtension())
            .storageKey(document.getStorageKey())
            .relatedEntityType(document.getRelatedEntityType())
            .relatedEntityId(document.getRelatedEntityId())
            .uploadedBy(document.getUploadedBy())
            .uploadedAt(document.getUploadedAt())
            .virusScanPassed(document.isVirusScanPassed())
            .scanResult(document.getScanResult())
            .extractionSuccessful(document.isExtractionSuccessful())
            .extractedData(document.getExtractedData())
            .extractionConfidence(document.getExtractionConfidence())
            .extractionModel(document.getExtractionModel())
            .verified(document.isVerified())
            .rejectionReason(document.getRejectionReason())
            .createdAt(document.getCreatedAt() != null ? 
                java.time.LocalDateTime.ofInstant(document.getCreatedAt(), java.time.ZoneId.systemDefault()) : null)
            .updatedAt(document.getUpdatedAt() != null ? 
                java.time.LocalDateTime.ofInstant(document.getUpdatedAt(), java.time.ZoneId.systemDefault()) : null)
            .build();
    }
}

