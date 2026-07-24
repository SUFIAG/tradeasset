package com.tradesphere.ai.document.adapter.in.web.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Document response DTO
 */
@Value
@Builder
public class DocumentResponse {
    String id;
    String documentType;
    String status;
    String fileName;
    String originalFileName;
    Long fileSize;
    String mimeType;
    String fileExtension;
    String storageKey;
    String relatedEntityType;
    String relatedEntityId;
    UUID uploadedBy;
    LocalDateTime uploadedAt;
    Boolean virusScanPassed;
    String scanResult;
    Boolean extractionSuccessful;
    Map<String, Object> extractedData;
    Float extractionConfidence;
    String extractionModel;
    Boolean verified;
    String rejectionReason;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}

