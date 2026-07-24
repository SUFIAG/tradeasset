package com.tradesphere.ai.document.infrastructure.rest;

import com.tradesphere.ai.document.application.service.extraction.DocumentExtractionService;
import com.tradesphere.ai.document.application.service.extraction.ExtractionQualityScorer;
import com.tradesphere.ai.document.application.service.extraction.ExtractionResult;
import com.tradesphere.ai.document.domain.model.DocumentType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * REST API for Document Extraction
 * Provides endpoints for OCR and AI-powered document extraction
 */
@RestController
@RequestMapping("/api/documents/extraction")
@Tag(name = "Document Extraction", description = "OCR and AI-powered document extraction APIs")
@RequiredArgsConstructor
@Slf4j
public class DocumentExtractionController {

    private final DocumentExtractionService extractionService;
    private final ExtractionQualityScorer qualityScorer;

    /**
     * Extract structured data from document
     */
    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Extract structured data from document",
        description = "Upload a document and extract structured data using OCR and AI"
    )
    public ResponseEntity<ExtractionResponse> extractDocument(
        @Parameter(description = "Document file (PDF, Image, etc.)")
        @RequestParam("file") MultipartFile file,

        @Parameter(description = "Document type for specialized extraction")
        @RequestParam("documentType") DocumentType documentType
    ) {
        log.info("Extraction request - File: {}, Type: {}, Size: {} bytes",
            file.getOriginalFilename(), documentType, file.getSize());

        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ExtractionResponse.error("File is empty"));
            }

            // Check if extraction is supported for this document type
            if (!extractionService.supportsExtraction(documentType)) {
                return ResponseEntity.badRequest()
                    .body(ExtractionResponse.error("Extraction not supported for document type: " + documentType));
            }

            // Perform extraction
            ExtractionResult result = extractionService.extractDocument(
                documentType,
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType()
            );

            if (!result.isSuccessful()) {
                log.warn("Extraction failed: {}", result.getErrorMessage());
                return ResponseEntity.ok(ExtractionResponse.error(result.getErrorMessage()));
            }

            // Score quality
            ExtractionQualityScorer.QualityScore qualityScore = qualityScorer.scoreExtraction(
                documentType,
                result.getExtractedData(),
                result.getConfidence()
            );

            log.info("Extraction successful - Confidence: {}, Quality: {}",
                result.getConfidence(), qualityScore.getQualityLevel());

            return ResponseEntity.ok(ExtractionResponse.success(
                result.getExtractedData(),
                result.getConfidence(),
                result.getModel(),
                qualityScore
            ));

        } catch (IOException e) {
            log.error("Failed to read uploaded file", e);
            return ResponseEntity.internalServerError()
                .body(ExtractionResponse.error("Failed to read file: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Extraction failed with unexpected error", e);
            return ResponseEntity.internalServerError()
                .body(ExtractionResponse.error("Extraction failed: " + e.getMessage()));
        }
    }

    /**
     * Check if extraction is supported for document type
     */
    @GetMapping("/supported/{documentType}")
    @Operation(
        summary = "Check extraction support",
        description = "Check if extraction is supported for a specific document type"
    )
    public ResponseEntity<SupportCheckResponse> checkSupport(
        @PathVariable DocumentType documentType
    ) {
        boolean supported = extractionService.supportsExtraction(documentType);
        return ResponseEntity.ok(new SupportCheckResponse(documentType, supported));
    }

    /**
     * Get list of supported document types
     */
    @GetMapping("/supported")
    @Operation(
        summary = "Get supported document types",
        description = "Get list of all document types that support extraction"
    )
    public ResponseEntity<DocumentType[]> getSupportedTypes() {
        // Filter document types that support extraction
        DocumentType[] allTypes = DocumentType.values();
        DocumentType[] supported = java.util.Arrays.stream(allTypes)
            .filter(extractionService::supportsExtraction)
            .toArray(DocumentType[]::new);

        return ResponseEntity.ok(supported);
    }

    /**
     * Extraction response DTO
     */
    @Value
    public static class ExtractionResponse {
        boolean success;
        Map<String, Object> extractedData;
        Float confidence;
        String model;
        QualityScoreDTO qualityScore;
        String errorMessage;

        public static ExtractionResponse success(
            Map<String, Object> data,
            Float confidence,
            String model,
            ExtractionQualityScorer.QualityScore qualityScore
        ) {
            return new ExtractionResponse(
                true,
                data,
                confidence,
                model,
                QualityScoreDTO.from(qualityScore),
                null
            );
        }

        public static ExtractionResponse error(String errorMessage) {
            return new ExtractionResponse(
                false,
                null,
                null,
                null,
                null,
                errorMessage
            );
        }
    }

    /**
     * Quality score DTO
     */
    @Value
    public static class QualityScoreDTO {
        float overallScore;
        String qualityLevel;
        float completenessScore;
        float validityScore;
        float consistencyScore;
        float confidenceScore;
        int issueCount;
        boolean requiresReview;
        boolean requiresManualCorrection;

        public static QualityScoreDTO from(ExtractionQualityScorer.QualityScore score) {
            return new QualityScoreDTO(
                score.getOverallScore(),
                score.getQualityLevel().toString(),
                score.getCompletenessScore(),
                score.getValidityScore(),
                score.getConsistencyScore(),
                score.getConfidenceScore(),
                score.getIssues().size(),
                score.isRequiresReview(),
                score.isRequiresManualCorrection()
            );
        }
    }

    /**
     * Support check response DTO
     */
    @Value
    public static class SupportCheckResponse {
        DocumentType documentType;
        boolean supported;
    }
}

