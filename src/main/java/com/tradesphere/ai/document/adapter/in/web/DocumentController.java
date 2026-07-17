package com.tradesphere.ai.document.adapter.in.web;

import com.tradesphere.ai.document.adapter.in.web.dto.DocumentDTOMapper;
import com.tradesphere.ai.document.adapter.in.web.dto.DocumentResponse;
import com.tradesphere.ai.document.application.port.in.*;
import com.tradesphere.ai.document.domain.model.Document;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

/**
 * REST Controller for Document Management
 * High-performance with streaming support
 */
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Document Management", description = "APIs for document upload, storage, and management")
public class DocumentController {

    private final UploadDocumentUseCase uploadDocumentUseCase;
    private final GetDocumentUseCase getDocumentUseCase;
    private final ListDocumentsUseCase listDocumentsUseCase;
    private final VerifyDocumentUseCase verifyDocumentUseCase;
    private final DownloadDocumentUseCase downloadDocumentUseCase;
    private final ExtractDocumentUseCase extractDocumentUseCase;
    private final DocumentDTOMapper dtoMapper;

    /**
     * Upload document with streaming support
     * Memory-efficient multipart file handling
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload document", description = "Upload a new document with virus scanning")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            @RequestParam(value = "relatedEntityType", required = false) String relatedEntityType,
            @RequestParam(value = "relatedEntityId", required = false) String relatedEntityId,
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Uploading document: {} type: {}", file.getOriginalFilename(), documentType);

        try {
            UploadDocumentUseCase.UploadDocumentCommand command =
                UploadDocumentUseCase.UploadDocumentCommand.builder()
                    .tenantId(tenantId)
                    .documentType(documentType)
                    .fileName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .mimeType(file.getContentType())
                    .fileStream(file.getInputStream())
                    .relatedEntityType(relatedEntityType)
                    .relatedEntityId(relatedEntityId)
                    .uploadedBy(userId)
                    .build();

            Document document = uploadDocumentUseCase.uploadDocument(command);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(dtoMapper.toResponse(document));

        } catch (Exception e) {
            log.error("Document upload failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Download document with streaming
     * Memory-efficient direct streaming
     */
    @GetMapping("/{documentId}/download")
    @Operation(summary = "Download document", description = "Download document file")
    public ResponseEntity<InputStreamResource> downloadDocument(@PathVariable String documentId) {
        log.info("Downloading document: {}", documentId);

        try {
            Document document = getDocumentUseCase.getDocumentById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

            InputStream fileStream = downloadDocumentUseCase.downloadDocument(documentId);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName() + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, document.getMimeType());

            return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(fileStream));

        } catch (Exception e) {
            log.error("Document download failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{documentId}")
    @Operation(summary = "Get document", description = "Get document metadata")
    public ResponseEntity<DocumentResponse> getDocument(@PathVariable String documentId) {
        log.debug("Getting document: {}", documentId);

        return getDocumentUseCase.getDocumentById(documentId)
            .map(dtoMapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "List documents", description = "List all documents with pagination")
    public ResponseEntity<Page<DocumentResponse>> listDocuments(Pageable pageable) {
        log.debug("Listing documents with pagination: {}", pageable);

        Page<DocumentResponse> documents = listDocumentsUseCase.listDocuments(pageable)
            .map(dtoMapper::toResponse);

        return ResponseEntity.ok(documents);
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    @Operation(summary = "List documents by entity", description = "List documents for specific entity")
    public ResponseEntity<Page<DocumentResponse>> listDocumentsByEntity(
            @PathVariable String entityType,
            @PathVariable String entityId,
            Pageable pageable) {

        log.debug("Listing documents for entity: {}/{}", entityType, entityId);

        Page<DocumentResponse> documents = listDocumentsUseCase
            .listDocumentsByEntity(entityType, entityId, pageable)
            .map(dtoMapper::toResponse);

        return ResponseEntity.ok(documents);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "List documents by status", description = "List documents with specific status")
    public ResponseEntity<Page<DocumentResponse>> listDocumentsByStatus(
            @PathVariable String status,
            Pageable pageable) {

        log.debug("Listing documents by status: {}", status);

        Page<DocumentResponse> documents = listDocumentsUseCase
            .listDocumentsByStatus(status, pageable)
            .map(dtoMapper::toResponse);

        return ResponseEntity.ok(documents);
    }

    @PostMapping("/{documentId}/verify")
    @Operation(summary = "Verify document", description = "Mark document as verified")
    public ResponseEntity<DocumentResponse> verifyDocument(
            @PathVariable String documentId,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Verifying document: {}", documentId);

        Document document = verifyDocumentUseCase.verifyDocument(documentId, userId);

        return ResponseEntity.ok(dtoMapper.toResponse(document));
    }

    @PostMapping("/{documentId}/reject")
    @Operation(summary = "Reject document", description = "Reject document with reason")
    public ResponseEntity<DocumentResponse> rejectDocument(
            @PathVariable String documentId,
            @RequestParam String reason,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Rejecting document: {}", documentId);

        Document document = verifyDocumentUseCase.rejectDocument(documentId, userId, reason);

        return ResponseEntity.ok(dtoMapper.toResponse(document));
    }

    @PostMapping("/{documentId}/extract")
    @Operation(summary = "Extract document", description = "Trigger AI extraction for document")
    public ResponseEntity<DocumentResponse> extractDocument(@PathVariable String documentId) {
        log.info("Triggering extraction for document: {}", documentId);

        Document document = extractDocumentUseCase.extractDocument(documentId);

        return ResponseEntity.ok(dtoMapper.toResponse(document));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage());
        return ResponseEntity.badRequest()
            .body(new ErrorResponse("INVALID_ARGUMENT", ex.getMessage()));
    }

    private record ErrorResponse(String code, String message) {}
}

