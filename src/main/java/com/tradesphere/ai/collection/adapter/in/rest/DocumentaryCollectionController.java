package com.tradesphere.ai.collection.adapter.in.rest;

import com.tradesphere.ai.collection.application.service.DocumentaryCollectionService;
import com.tradesphere.ai.collection.domain.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/documentary-collections")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Documentary Collections", description = "Documentary Collection management API")
public class DocumentaryCollectionController {

    private final DocumentaryCollectionService service;
    private final DocumentaryCollectionDtoMapper dtoMapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('DC_CREATOR', 'TRADE_FINANCE_OFFICER')")
    @Operation(summary = "Create Documentary Collection")
    public ResponseEntity<CollectionResponse> createCollection(
            @Valid @RequestBody CreateCollectionRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId
    ) {
        log.info("Creating documentary collection: drawer={}, amount={}", request.getDrawerId(), request.getAmount());

        DocumentaryCollection collection = service.createCollection(
            tenantId,
            request.getReferenceNumber(),
            request.getDrawerId(),
            request.getDraweeId(),
            request.getCollectionType(),
            request.getAmount(),
            request.getCurrency(),
            request.getPaymentTerms(),
            request.getGoodsDescription(),
            userId
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toResponse(collection));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DC_VIEWER', 'TRADE_FINANCE_OFFICER')")
    @Operation(summary = "Get Documentary Collection")
    public ResponseEntity<CollectionResponse> getCollection(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId
    ) {
        DocumentaryCollection collection = service.getCollection(id, tenantId);
        return ResponseEntity.ok(dtoMapper.toResponse(collection));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DC_VIEWER', 'TRADE_FINANCE_OFFICER')")
    @Operation(summary = "List Documentary Collections")
    public ResponseEntity<List<CollectionResponse>> listCollections(
            @RequestHeader("X-Tenant-ID") String tenantId
    ) {
        List<CollectionResponse> collections = service.listCollections(tenantId).stream()
            .map(dtoMapper::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(collections);
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('DC_CREATOR', 'TRADE_FINANCE_OFFICER')")
    @Operation(summary = "Submit Collection")
    public ResponseEntity<Void> submitCollection(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId
    ) {
        service.submitCollection(id, tenantId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/present")
    @PreAuthorize("hasAnyRole('DC_PRESENTER', 'TRADE_FINANCE_OFFICER')")
    @Operation(summary = "Present Documents")
    public ResponseEntity<Void> presentDocuments(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId
    ) {
        service.presentDocuments(id, tenantId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasAnyRole('DC_ACCEPTOR', 'TRADE_FINANCE_OFFICER')")
    @Operation(summary = "Accept Collection")
    public ResponseEntity<Void> acceptCollection(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId
    ) {
        service.acceptCollection(id, tenantId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/payment")
    @PreAuthorize("hasAnyRole('DC_PAYMENT', 'TRADE_FINANCE_OFFICER')")
    @Operation(summary = "Process Payment")
    public ResponseEntity<Void> processPayment(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId,
            @Valid @RequestBody PaymentRequest request
    ) {
        service.processPayment(id, tenantId, request.getAmount(), userId);
        return ResponseEntity.ok().build();
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCollectionRequest {
        private String referenceNumber;
        private UUID drawerId;
        private UUID draweeId;
        private CollectionType collectionType;
        private BigDecimal amount;
        private String currency;
        private PaymentTerms paymentTerms;
        private String goodsDescription;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentRequest {
        private BigDecimal amount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CollectionResponse {
        private UUID id;
        private String tenantId;
        private String collectionNumber;
        private String referenceNumber;
        private UUID drawerId;
        private UUID draweeId;
        private CollectionType collectionType;
        private BigDecimal amount;
        private String currency;
        private PaymentTerms paymentTerms;
        private CollectionStatus status;
        private LocalDateTime collectionDate;
        private LocalDateTime presentationDate;
        private LocalDate paymentDueDate;
        private BigDecimal amountReceived;
        private BigDecimal outstandingAmount;
    }
}

