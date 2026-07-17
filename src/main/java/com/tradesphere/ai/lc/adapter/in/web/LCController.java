package com.tradesphere.ai.lc.adapter.in.web;

import com.tradesphere.ai.lc.adapter.in.web.dto.CreateLCRequest;
import com.tradesphere.ai.lc.adapter.in.web.dto.LCResponse;
import com.tradesphere.ai.lc.adapter.in.web.dto.BulkLCOperationRequest;
import com.tradesphere.ai.lc.adapter.in.web.dto.BulkLCOperationResponse;
import com.tradesphere.ai.lc.adapter.in.web.mapper.LCDTOMapper;
import com.tradesphere.ai.lc.application.port.in.*;
import com.tradesphere.ai.lc.application.service.SwiftMT700Generator;
import com.tradesphere.ai.lc.domain.model.LCException;
import com.tradesphere.ai.lc.domain.model.LetterOfCredit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Letter of Credit Management
 */
@RestController
@RequestMapping("/api/v1/trade/lc")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Letter of Credit", description = "APIs for managing Letters of Credit")
public class LCController {

    private final CreateLCUseCase createLCUseCase;
    private final GetLCUseCase getLCUseCase;
    private final ListLCsUseCase listLCsUseCase;
    private final SubmitLCUseCase submitLCUseCase;
    private final ApproveLCUseCase approveLCUseCase;
    private final RejectLCUseCase rejectLCUseCase;
    private final IssueLCUseCase issueLCUseCase;
    private final ReceiveDocumentsUseCase receiveDocumentsUseCase;
    private final AcceptDocumentsUseCase acceptDocumentsUseCase;
    private final RejectDocumentsUseCase rejectDocumentsUseCase;
    private final WaiveDiscrepanciesUseCase waiveDiscrepanciesUseCase;
    private final AuthorizePaymentUseCase authorizePaymentUseCase;
    private final CompletePaymentUseCase completePaymentUseCase;
    private final RequestAmendmentUseCase requestAmendmentUseCase;
    private final ApproveAmendmentUseCase approveAmendmentUseCase;
    private final RejectAmendmentUseCase rejectAmendmentUseCase;
    private final SwiftMT700Generator swiftMT700Generator;
    private final LCDTOMapper dtoMapper;

    @PostMapping
    @Operation(summary = "Create new LC application", description = "Creates a new Letter of Credit application")
    public ResponseEntity<LCResponse> createLC(
            @Valid @RequestBody CreateLCRequest request,
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Creating LC for applicant: {}", request.getApplicantId());

        LetterOfCredit lc = createLCUseCase.createLC(
                dtoMapper.toCommand(request, tenantId, userId)
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dtoMapper.toResponse(lc));
    }

    @GetMapping("/{lcId}")
    @Operation(summary = "Get LC by ID", description = "Retrieves LC details by ID")
    public ResponseEntity<LCResponse> getLC(@PathVariable String lcId) {
        log.info("Getting LC: {}", lcId);

        return getLCUseCase.getLCById(lcId)
                .map(lc -> ResponseEntity.ok(dtoMapper.toResponse(lc)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{lcNumber}")
    @Operation(summary = "Get LC by number", description = "Retrieves LC details by LC number")
    public ResponseEntity<LCResponse> getLCByNumber(@PathVariable String lcNumber) {
        log.info("Getting LC by number: {}", lcNumber);

        return getLCUseCase.getLCByNumber(lcNumber)
                .map(lc -> ResponseEntity.ok(dtoMapper.toResponse(lc)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "List all LCs", description = "Lists all LCs with pagination")
    public ResponseEntity<Page<LCResponse>> listLCs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {

        log.info("Listing LCs - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<LetterOfCredit> lcs = listLCsUseCase.listLCs(pageable);
        Page<LCResponse> response = lcs.map(dtoMapper::toResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/applicant/{applicantId}")
    @Operation(summary = "List LCs by applicant", description = "Lists LCs for a specific customer")
    public ResponseEntity<Page<LCResponse>> listLCsByApplicant(
            @PathVariable String applicantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Listing LCs for applicant: {}", applicantId);

        Pageable pageable = PageRequest.of(page, size);
        Page<LetterOfCredit> lcs = listLCsUseCase.listLCsByApplicant(applicantId, pageable);
        Page<LCResponse> response = lcs.map(dtoMapper::toResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-status/{status}")
    @Operation(summary = "List LCs by status", description = "Lists LCs filtered by status")
    public ResponseEntity<Page<LCResponse>> listLCsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Listing LCs by status: {}", status);

        Pageable pageable = PageRequest.of(page, size);
        Page<LetterOfCredit> lcs = listLCsUseCase.listLCsByStatus(status, pageable);
        Page<LCResponse> response = lcs.map(dtoMapper::toResponse);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{lcId}/submit")
    @Operation(summary = "Submit LC for approval", description = "Submits LC application for approval")
    public ResponseEntity<LCResponse> submitForApproval(
            @PathVariable String lcId,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Submitting LC for approval: {}", lcId);

        LetterOfCredit lc = submitLCUseCase.submitForApproval(lcId, userId);

        return ResponseEntity.ok(dtoMapper.toResponse(lc));
    }

    @PostMapping("/{lcId}/approve")
    @Operation(summary = "Approve LC", description = "Approves an LC application")
    public ResponseEntity<LCResponse> approveLC(
            @PathVariable String lcId,
            @RequestParam(defaultValue = "true") boolean creditLimitApproved,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Approving LC: {}", lcId);

        LetterOfCredit lc = approveLCUseCase.approveLC(
                ApproveLCUseCase.ApproveLCCommand.builder()
                        .lcId(lcId)
                        .approvedBy(userId)
                        .creditLimitApproved(creditLimitApproved)
                        .build()
        );

        return ResponseEntity.ok(dtoMapper.toResponse(lc));
    }

    @PostMapping("/{lcId}/reject")
    @Operation(summary = "Reject LC", description = "Rejects an LC application")
    public ResponseEntity<LCResponse> rejectLC(
            @PathVariable String lcId,
            @RequestParam String reason,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Rejecting LC: {}", lcId);

        LetterOfCredit lc = rejectLCUseCase.rejectLC(lcId, reason, userId);

        return ResponseEntity.ok(dtoMapper.toResponse(lc));
    }

    @PostMapping("/{lcId}/issue")
    @Operation(summary = "Issue LC", description = "Issues an approved LC")
    public ResponseEntity<LCResponse> issueLC(
            @PathVariable String lcId,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Issuing LC: {}", lcId);

        LetterOfCredit lc = issueLCUseCase.issueLC(lcId, userId);

        return ResponseEntity.ok(dtoMapper.toResponse(lc));
    }

    @PostMapping("/{lcId}/documents/receive")
    @Operation(summary = "Receive LC documents", description = "Marks documents as received for an issued LC")
    public ResponseEntity<LCResponse> receiveDocuments(
            @PathVariable String lcId,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Receiving documents for LC: {}", lcId);
        LetterOfCredit lc = receiveDocumentsUseCase.receiveDocuments(lcId, userId);
        return ResponseEntity.ok(dtoMapper.toResponse(lc));
    }

    @PostMapping("/{lcId}/documents/accept")
    @Operation(summary = "Accept LC documents", description = "Accepts documents submitted for an LC")
    public ResponseEntity<LCResponse> acceptDocuments(
            @PathVariable String lcId,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Accepting documents for LC: {}", lcId);
        LetterOfCredit lc = acceptDocumentsUseCase.acceptDocuments(lcId, userId);
        return ResponseEntity.ok(dtoMapper.toResponse(lc));
    }

    @PostMapping("/{lcId}/documents/reject")
    @Operation(summary = "Reject LC documents", description = "Rejects documents due to discrepancies")
    public ResponseEntity<LCResponse> rejectDocuments(
            @PathVariable String lcId,
            @RequestParam String discrepancies,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Rejecting documents for LC: {}", lcId);
        LetterOfCredit lc = rejectDocumentsUseCase.rejectDocuments(lcId, discrepancies, userId);
        return ResponseEntity.ok(dtoMapper.toResponse(lc));
    }

    @PostMapping("/{lcId}/documents/waive-discrepancies")
    @Operation(summary = "Waive document discrepancies", description = "Waives discrepancies and proceeds with payment")
    public ResponseEntity<LCResponse> waiveDiscrepancies(
            @PathVariable String lcId,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Waiving discrepancies for LC: {}", lcId);
        LetterOfCredit lc = waiveDiscrepanciesUseCase.waiveDiscrepancies(lcId, userId);
        return ResponseEntity.ok(dtoMapper.toResponse(lc));
    }

    @PostMapping("/{lcId}/payment/authorize")
    @Operation(summary = "Authorize LC payment", description = "Authorizes payment for accepted documents")
    public ResponseEntity<LCResponse> authorizePayment(
            @PathVariable String lcId,
            @RequestParam java.math.BigDecimal amount,
            @RequestParam String currency,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Authorizing payment for LC: {}", lcId);

        com.tradesphere.ai.shared.domain.valueobject.Money paymentAmount =
            com.tradesphere.ai.shared.domain.valueobject.Money.of(amount, currency);

        LetterOfCredit lc = authorizePaymentUseCase.authorizePayment(lcId, paymentAmount, userId);
        return ResponseEntity.ok(dtoMapper.toResponse(lc));
    }

    @PostMapping("/{lcId}/payment/complete")
    @Operation(summary = "Complete LC payment", description = "Marks payment as completed for an LC")
    public ResponseEntity<LCResponse> completePayment(
            @PathVariable String lcId,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Completing payment for LC: {}", lcId);
        LetterOfCredit lc = completePaymentUseCase.completePayment(lcId, userId);
        return ResponseEntity.ok(dtoMapper.toResponse(lc));
    }

    @PostMapping("/{lcId}/amendment")
    @Operation(summary = "Request LC amendment", description = "Requests amendment to an issued LC")
    public ResponseEntity<LCResponse> requestAmendment(
            @PathVariable String lcId,
            @RequestParam String amendmentDetails,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Requesting amendment for LC: {}", lcId);
        LetterOfCredit lc = requestAmendmentUseCase.requestAmendment(lcId, amendmentDetails, userId);
        return ResponseEntity.ok(dtoMapper.toResponse(lc));
    }

    @PostMapping("/{lcId}/amendment/approve")
    @Operation(summary = "Approve LC amendment", description = "Approves requested LC amendment")
    public ResponseEntity<LCResponse> approveAmendment(
            @PathVariable String lcId,
            @RequestParam(required = false) java.math.BigDecimal newAmount,
            @RequestParam(required = false) String newCurrency,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate newExpiryDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate newLastShipmentDate,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Approving amendment for LC: {}", lcId);

        com.tradesphere.ai.shared.domain.valueobject.Money money = null;
        if (newAmount != null && newCurrency != null) {
            money = com.tradesphere.ai.shared.domain.valueobject.Money.of(newAmount, newCurrency);
        }

        LetterOfCredit lc = approveAmendmentUseCase.approveAmendment(
            lcId, money, newExpiryDate, newLastShipmentDate, userId
        );
        return ResponseEntity.ok(dtoMapper.toResponse(lc));
    }

    @PostMapping("/{lcId}/amendment/reject")
    @Operation(summary = "Reject LC amendment", description = "Rejects requested LC amendment")
    public ResponseEntity<LCResponse> rejectAmendment(
            @PathVariable String lcId,
            @RequestParam String rejectionReason,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Rejecting amendment for LC: {}", lcId);
        LetterOfCredit lc = rejectAmendmentUseCase.rejectAmendment(lcId, rejectionReason, userId);
        return ResponseEntity.ok(dtoMapper.toResponse(lc));
    }

    @GetMapping("/{lcId}/swift/mt700")
    @Operation(summary = "Generate SWIFT MT700", description = "Generates SWIFT MT700 message for LC")
    public ResponseEntity<String> generateSwiftMT700(@PathVariable String lcId) {
        log.info("Generating SWIFT MT700 for LC: {}", lcId);

        LetterOfCredit lc = getLCUseCase.getLCById(lcId)
                .orElseThrow(() -> new IllegalArgumentException("LC not found: " + lcId));

        String swiftMessage = swiftMT700Generator.generateMT700(lc);

        return ResponseEntity.ok(swiftMessage);
    }

    @GetMapping("/{lcId}/swift/mt707")
    @Operation(summary = "Generate SWIFT MT707", description = "Generates SWIFT MT707 amendment message for LC")
    public ResponseEntity<String> generateSwiftMT707(
            @PathVariable String lcId,
            @RequestParam String amendmentDetails) {
        log.info("Generating SWIFT MT707 amendment for LC: {}", lcId);

        LetterOfCredit lc = getLCUseCase.getLCById(lcId)
                .orElseThrow(() -> new IllegalArgumentException("LC not found: " + lcId));

        String swiftMessage = swiftMT700Generator.generateMT707(lc, amendmentDetails);

        return ResponseEntity.ok(swiftMessage);
    }

    @ExceptionHandler(LCException.class)
    public ResponseEntity<ErrorResponse> handleLCException(LCException ex) {
        log.error("LC exception: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                "LC_ERROR",
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Invalid argument: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                "INVALID_ARGUMENT",
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return ResponseEntity.badRequest().body(error);
    }

    private record ErrorResponse(String code, String message, long timestamp) {}

    /**
     * Bulk approve LCs - High-performance batch operation
     */
    @PostMapping("/bulk/approve")
    public ResponseEntity<BulkLCOperationResponse> bulkApproveLCs(
            @RequestBody @Valid BulkLCOperationRequest request) {

        log.info("Bulk approve {} LCs", request.getLcIds().size());

        List<BulkLCOperationResponse.OperationResult> results = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (String lcId : request.getLcIds()) {
            try {
                LetterOfCredit lc = approveLCUseCase.approveLC(
                        ApproveLCUseCase.ApproveLCCommand.builder()
                                .lcId(lcId)
                                .approvedBy(request.getOperatedBy())
                                .creditLimitApproved(true)
                                .build());
                results.add(BulkLCOperationResponse.OperationResult.builder()
                        .lcId(lcId)
                        .lcNumber(lc.getLcNumber() != null ? lc.getLcNumber().value() : null)
                        .success(true)
                        .build());
                successCount++;
            } catch (Exception e) {
                log.error("Failed to approve LC: {}", lcId, e);
                results.add(BulkLCOperationResponse.OperationResult.builder()
                        .lcId(lcId)
                        .success(false)
                        .errorMessage(e.getMessage())
                        .build());
                failureCount++;
            }
        }

        BulkLCOperationResponse response = BulkLCOperationResponse.builder()
                .totalRequested(request.getLcIds().size())
                .successCount(successCount)
                .failureCount(failureCount)
                .results(results)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Check and expire overdue LCs - Scheduled batch operation
     */
    @PostMapping("/bulk/check-expiry")
    public ResponseEntity<BulkLCOperationResponse> checkAndExpireLCs() {
        log.info("Checking for expired LCs");

        // Get all active LCs
        Page<LetterOfCredit> activeLCs = listLCsUseCase.listLCs(
                PageRequest.of(0, 1000)); // Batch size for performance

        List<BulkLCOperationResponse.OperationResult> results = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (LetterOfCredit lc : activeLCs) {
            try {
                if (lc.isExpired() && lc.getStatus() != null) {
                    lc.expire();
                    results.add(BulkLCOperationResponse.OperationResult.builder()
                            .lcId(lc.getId().asString())
                            .lcNumber(lc.getLcNumber() != null ? lc.getLcNumber().value() : null)
                            .success(true)
                            .build());
                    successCount++;
                }
            } catch (Exception e) {
                log.error("Failed to expire LC: {}", lc.getId(), e);
                results.add(BulkLCOperationResponse.OperationResult.builder()
                        .lcId(lc.getId().asString())
                        .success(false)
                        .errorMessage(e.getMessage())
                        .build());
                failureCount++;
            }
        }

        BulkLCOperationResponse response = BulkLCOperationResponse.builder()
                .totalRequested((int) activeLCs.getTotalElements())
                .successCount(successCount)
                .failureCount(failureCount)
                .results(results)
                .build();

        return ResponseEntity.ok(response);
    }
}
