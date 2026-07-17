package com.tradesphere.ai.bankguarantee.adapter.in.rest;

import com.tradesphere.ai.bankguarantee.adapter.in.rest.dto.*;
import com.tradesphere.ai.bankguarantee.application.port.in.*;
import com.tradesphere.ai.bankguarantee.application.port.in.dto.*;
import com.tradesphere.ai.bankguarantee.domain.model.BGType;
import com.tradesphere.ai.shared.web.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST API Controller for Bank Guarantees
 * Provides endpoints for complete BG lifecycle management
 */
@RestController
@RequestMapping("/api/v1/bank-guarantees")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bank Guarantees", description = "Bank Guarantee management API")
public class BankGuaranteeController {

    private final CreateBankGuaranteeUseCase createBankGuaranteeUseCase;
    private final GetBankGuaranteeUseCase getBankGuaranteeUseCase;
    private final SubmitBankGuaranteeUseCase submitBankGuaranteeUseCase;
    private final ApproveBankGuaranteeUseCase approveBankGuaranteeUseCase;
    private final IssueBankGuaranteeUseCase issueBankGuaranteeUseCase;
    private final AmendBankGuaranteeUseCase amendBankGuaranteeUseCase;
    private final ProcessClaimUseCase processClaimUseCase;
    private final CancelBankGuaranteeUseCase cancelBankGuaranteeUseCase;
    private final ReleaseBankGuaranteeUseCase releaseBankGuaranteeUseCase;
    private final ExtendBankGuaranteeUseCase extendBankGuaranteeUseCase;
    private final ReduceBankGuaranteeUseCase reduceBankGuaranteeUseCase;

    /**
     * Create new Bank Guarantee application
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('BG_CREATOR', 'TRADE_FINANCE_OFFICER')")
    @Operation(summary = "Create Bank Guarantee", description = "Create a new bank guarantee application")
    public ResponseEntity<BankGuaranteeResponse> createBankGuarantee(
            @Valid @RequestBody CreateBankGuaranteeRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId
    ) {
        log.info("Creating bank guarantee: type={}, amount={}, tenant={}",
            request.getBgType(), request.getAmount(), tenantId);

        CreateBankGuaranteeCommand command = CreateBankGuaranteeCommand.builder()
            .tenantId(tenantId)
            .referenceNumber(request.getReferenceNumber())
            .applicantId(request.getApplicantId())
            .beneficiaryId(request.getBeneficiaryId())
            .bgType(request.getBgType())
            .amount(request.getAmount())
            .currency(request.getCurrency())
            .expiryDate(request.getExpiryDate())
            .purpose(request.getPurpose())
            .governingLaw(request.getGoverningLaw())
            .jurisdiction(request.getJurisdiction())
            .specialConditions(request.getSpecialConditions())
            .requiredDocuments(request.getRequiredDocuments())
            .reductionAllowed(request.isReductionAllowed())
            .extensionAllowed(request.isExtensionAllowed())
            .userId(userId)
            .build();

        BankGuaranteeResponse response = createBankGuaranteeUseCase.execute(command);

        log.info("Bank guarantee created: id={}, reference={}",
            response.getId(), response.getReferenceNumber());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get Bank Guarantee by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('BG_VIEWER', 'TRADE_FINANCE_OFFICER')")
    @Operation(summary = "Get Bank Guarantee", description = "Retrieve bank guarantee by ID")
    public ResponseEntity<BankGuaranteeResponse> getBankGuarantee(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId
    ) {
        log.debug("Retrieving bank guarantee: id={}, tenant={}", id, tenantId);

        BankGuaranteeResponse response = getBankGuaranteeUseCase.execute(id, tenantId);
        return ResponseEntity.ok(response);
    }

    /**
     * List all Bank Guarantees for tenant
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('BG_VIEWER', 'TRADE_FINANCE_OFFICER')")
    @Operation(summary = "List Bank Guarantees", description = "List all bank guarantees for tenant")
    public ResponseEntity<List<BankGuaranteeResponse>> listBankGuarantees(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestParam(required = false) BGType type,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        log.debug("Listing bank guarantees: tenant={}, type={}, status={}", tenantId, type, status);

        List<BankGuaranteeResponse> guarantees = getBankGuaranteeUseCase.listAll(tenantId, type, status, page, size);
        return ResponseEntity.ok(guarantees);
    }

    /**
     * Submit Bank Guarantee for approval
     */
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('BG_CREATOR', 'TRADE_FINANCE_OFFICER')")
    @Operation(summary = "Submit BG", description = "Submit bank guarantee for approval")
    public ResponseEntity<Void> submitBankGuarantee(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId
    ) {
        log.info("Submitting bank guarantee: id={}, user={}", id, userId);

        SubmitBankGuaranteeCommand command = new SubmitBankGuaranteeCommand(id, tenantId, userId);
        submitBankGuaranteeUseCase.execute(command);

        return ResponseEntity.ok().build();
    }

    /**
     * Approve Bank Guarantee
     */
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('BG_APPROVER')")
    @Operation(summary = "Approve BG", description = "Approve bank guarantee")
    public ResponseEntity<Void> approveBankGuarantee(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId,
            @RequestBody(required = false) ApprovalRequest request
    ) {
        log.info("Approving bank guarantee: id={}, approver={}", id, userId);

        ApproveBankGuaranteeCommand command = ApproveBankGuaranteeCommand.builder()
            .bgId(id)
            .tenantId(tenantId)
            .approverId(userId)
            .comments(request != null ? request.getComments() : null)
            .build();

        approveBankGuaranteeUseCase.execute(command);

        return ResponseEntity.ok().build();
    }

    /**
     * Reject Bank Guarantee
     */
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('BG_APPROVER')")
    @Operation(summary = "Reject BG", description = "Reject bank guarantee")
    public ResponseEntity<Void> rejectBankGuarantee(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId,
            @Valid @RequestBody RejectionRequest request
    ) {
        log.info("Rejecting bank guarantee: id={}, approver={}", id, userId);

        RejectBankGuaranteeCommand command = new RejectBankGuaranteeCommand(
            id, tenantId, userId, request.getReason()
        );

        approveBankGuaranteeUseCase.reject(command);

        return ResponseEntity.ok().build();
    }

    /**
     * Issue Bank Guarantee
     */
    @PostMapping("/{id}/issue")
    @PreAuthorize("hasRole('BG_ISSUER')")
    @Operation(summary = "Issue BG", description = "Issue bank guarantee")
    public ResponseEntity<Void> issueBankGuarantee(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId,
            @Valid @RequestBody IssueBGRequest request
    ) {
        log.info("Issuing bank guarantee: id={}, issuer={}", id, userId);

        IssueBankGuaranteeCommand command = IssueBankGuaranteeCommand.builder()
            .bgId(id)
            .tenantId(tenantId)
            .issuerId(userId)
            .issuingBank(request.getIssuingBank())
            .advisingBank(request.getAdvisingBank())
            .issueDate(request.getIssueDate() != null ? request.getIssueDate() : LocalDateTime.now())
            .effectiveDate(request.getEffectiveDate())
            .build();

        issueBankGuaranteeUseCase.execute(command);

        return ResponseEntity.ok().build();
    }

    /**
     * Request Amendment
     */
    @PostMapping("/{id}/amendment")
    @PreAuthorize("hasAnyRole('BG_CREATOR', 'TRADE_FINANCE_OFFICER')")
    @Operation(summary = "Request Amendment", description = "Request bank guarantee amendment")
    public ResponseEntity<UUID> requestAmendment(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId,
            @Valid @RequestBody AmendmentRequest request
    ) {
        log.info("Requesting amendment: bgId={}, type={}", id, request.getAmendmentType());

        RequestAmendmentCommand command = RequestAmendmentCommand.builder()
            .bgId(id)
            .tenantId(tenantId)
            .userId(userId)
            .amendmentType(request.getAmendmentType())
            .newAmount(request.getNewAmount())
            .newExpiryDate(request.getNewExpiryDate())
            .changesDescription(request.getChangesDescription())
            .build();

        UUID amendmentId = amendBankGuaranteeUseCase.requestAmendment(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(amendmentId);
    }

    /**
     * Approve Amendment
     */
    @PostMapping("/{id}/amendment/{amendmentId}/approve")
    @PreAuthorize("hasRole('BG_APPROVER')")
    @Operation(summary = "Approve Amendment", description = "Approve bank guarantee amendment")
    public ResponseEntity<Void> approveAmendment(
            @PathVariable UUID id,
            @PathVariable UUID amendmentId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId
    ) {
        log.info("Approving amendment: amendmentId={}, approver={}", amendmentId, userId);

        amendBankGuaranteeUseCase.approveAmendment(amendmentId, tenantId, userId);

        return ResponseEntity.ok().build();
    }

    /**
     * Register Claim
     */
    @PostMapping("/{id}/claim")
    @PreAuthorize("hasAnyRole('BG_BENEFICIARY', 'TRADE_FINANCE_OFFICER')")
    @Operation(summary = "Register Claim", description = "Register claim against bank guarantee")
    public ResponseEntity<UUID> registerClaim(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId,
            @Valid @RequestBody ClaimRequest request
    ) {
        log.info("Registering claim: bgId={}, amount={}", id, request.getClaimAmount());

        RegisterClaimCommand command = RegisterClaimCommand.builder()
            .bgId(id)
            .tenantId(tenantId)
            .userId(userId)
            .claimAmount(request.getClaimAmount())
            .claimCurrency(request.getClaimCurrency())
            .claimReason(request.getClaimReason())
            .claimType(request.getClaimType())
            .supportingDocuments(request.getSupportingDocuments())
            .build();

        UUID claimId = processClaimUseCase.registerClaim(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(claimId);
    }

    /**
     * Approve Claim
     */
    @PostMapping("/{id}/claim/{claimId}/approve")
    @PreAuthorize("hasRole('BG_CLAIM_APPROVER')")
    @Operation(summary = "Approve Claim", description = "Approve claim against bank guarantee")
    public ResponseEntity<Void> approveClaim(
            @PathVariable UUID id,
            @PathVariable UUID claimId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId,
            @Valid @RequestBody ApproveClaimRequest request
    ) {
        log.info("Approving claim: claimId={}, approver={}", claimId, userId);

        processClaimUseCase.approveClaim(claimId, tenantId, userId, request.getApprovedAmount());

        return ResponseEntity.ok().build();
    }

    /**
     * Extend Bank Guarantee
     */
    @PostMapping("/{id}/extend")
    @PreAuthorize("hasAnyRole('BG_CREATOR', 'TRADE_FINANCE_OFFICER')")
    @Operation(summary = "Extend BG", description = "Request bank guarantee extension")
    public ResponseEntity<UUID> extendBankGuarantee(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId,
            @Valid @RequestBody ExtensionRequest request
    ) {
        log.info("Requesting extension: bgId={}, newExpiry={}", id, request.getNewExpiryDate());

        RequestExtensionCommand command = RequestExtensionCommand.builder()
            .bgId(id)
            .tenantId(tenantId)
            .userId(userId)
            .newExpiryDate(request.getNewExpiryDate())
            .extensionReason(request.getExtensionReason())
            .justification(request.getJustification())
            .build();

        UUID extensionId = extendBankGuaranteeUseCase.requestExtension(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(extensionId);
    }

    /**
     * Reduce Bank Guarantee Amount
     */
    @PostMapping("/{id}/reduce")
    @PreAuthorize("hasAnyRole('BG_CREATOR', 'TRADE_FINANCE_OFFICER')")
    @Operation(summary = "Reduce BG", description = "Request bank guarantee reduction")
    public ResponseEntity<UUID> reduceBankGuarantee(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId,
            @Valid @RequestBody ReductionRequest request
    ) {
        log.info("Requesting reduction: bgId={}, newAmount={}", id, request.getNewTotalAmount());

        RequestReductionCommand command = RequestReductionCommand.builder()
            .bgId(id)
            .tenantId(tenantId)
            .userId(userId)
            .reductionAmount(request.getReductionAmount())
            .newTotalAmount(request.getNewTotalAmount())
            .reductionReason(request.getReductionReason())
            .supportingEvidence(request.getSupportingEvidence())
            .build();

        UUID reductionId = reduceBankGuaranteeUseCase.requestReduction(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(reductionId);
    }

    /**
     * Cancel Bank Guarantee
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('BG_CANCELLER')")
    @Operation(summary = "Cancel BG", description = "Cancel bank guarantee")
    public ResponseEntity<Void> cancelBankGuarantee(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId,
            @Valid @RequestBody CancellationRequest request
    ) {
        log.info("Cancelling bank guarantee: id={}, user={}", id, userId);

        CancelBankGuaranteeCommand command = new CancelBankGuaranteeCommand(
            id, tenantId, userId, request.getReason()
        );

        cancelBankGuaranteeUseCase.execute(command);

        return ResponseEntity.ok().build();
    }

    /**
     * Release Bank Guarantee
     */
    @PostMapping("/{id}/release")
    @PreAuthorize("hasAnyRole('BG_BENEFICIARY', 'BG_RELEASER')")
    @Operation(summary = "Release BG", description = "Release bank guarantee (beneficiary releases claim)")
    public ResponseEntity<Void> releaseBankGuarantee(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId
    ) {
        log.info("Releasing bank guarantee: id={}, user={}", id, userId);

        ReleaseBankGuaranteeCommand command = new ReleaseBankGuaranteeCommand(id, tenantId, userId);

        releaseBankGuaranteeUseCase.execute(command);

        return ResponseEntity.ok().build();
    }

    /**
     * Get expiring guarantees
     */
    @GetMapping("/expiring")
    @PreAuthorize("hasAnyRole('BG_VIEWER', 'TRADE_FINANCE_OFFICER')")
    @Operation(summary = "Get Expiring BGs", description = "Get bank guarantees expiring soon")
    public ResponseEntity<List<BankGuaranteeResponse>> getExpiringGuarantees(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestParam(defaultValue = "30") int days
    ) {
        log.debug("Getting expiring guarantees: tenant={}, days={}", tenantId, days);

        List<BankGuaranteeResponse> expiring = getBankGuaranteeUseCase.getExpiringSoon(tenantId, days);
        return ResponseEntity.ok(expiring);
    }

    /**
     * Get active claims
     */
    @GetMapping("/{id}/claims")
    @PreAuthorize("hasAnyRole('BG_VIEWER', 'TRADE_FINANCE_OFFICER')")
    @Operation(summary = "Get Claims", description = "Get all claims for a bank guarantee")
    public ResponseEntity<List<ClaimResponse>> getClaims(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId
    ) {
        log.debug("Getting claims for BG: id={}", id);

        List<ClaimResponse> claims = processClaimUseCase.getClaims(id, tenantId);
        return ResponseEntity.ok(claims);
    }

    /**
     * Get amendments
     */
    @GetMapping("/{id}/amendments")
    @PreAuthorize("hasAnyRole('BG_VIEWER', 'TRADE_FINANCE_OFFICER')")
    @Operation(summary = "Get Amendments", description = "Get all amendments for a bank guarantee")
    public ResponseEntity<List<AmendmentResponse>> getAmendments(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId
    ) {
        log.debug("Getting amendments for BG: id={}", id);

        List<AmendmentResponse> amendments = amendBankGuaranteeUseCase.getAmendments(id, tenantId);
        return ResponseEntity.ok(amendments);
    }
}

