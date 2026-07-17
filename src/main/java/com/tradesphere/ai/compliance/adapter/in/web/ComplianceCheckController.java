package com.tradesphere.ai.compliance.adapter.in.web;

import com.tradesphere.ai.compliance.application.port.in.*;
import com.tradesphere.ai.compliance.domain.model.ComplianceCheck;
import com.tradesphere.ai.compliance.domain.model.ComplianceCheckId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST API for compliance checks
 */
@RestController
@RequestMapping("/api/compliance/checks")
@RequiredArgsConstructor
@Slf4j
public class ComplianceCheckController {

    private final InitiateComplianceCheckUseCase initiateCheckUseCase;
    private final ApproveComplianceCheckUseCase approveCheckUseCase;
    private final RejectComplianceCheckUseCase rejectCheckUseCase;
    private final ClearFalsePositiveUseCase clearFalsePositiveUseCase;
    private final GetComplianceCheckUseCase getCheckUseCase;

    /**
     * Initiate a new compliance check
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('COMPLIANCE_OFFICER', 'SYSTEM_ADMIN')")
    public ResponseEntity<ComplianceCheckResponse> initiateCheck(
            @RequestBody InitiateComplianceCheckRequest request,
            @RequestHeader("X-Tenant-ID") UUID tenantId) {

        log.info("Initiating compliance check via API: entityType={}, entityId={}",
                request.getEntityType(), request.getEntityId());

        InitiateComplianceCheckCommand command = new InitiateComplianceCheckCommand(
                tenantId,
                request.getCheckType(),
                request.getEntityType(),
                request.getEntityId(),
                request.getEntityName(),
                request.getInitiatedBy(),
                request.getCountry(),
                request.getDateOfBirth(),
                request.getPosition(),
                request.getTransactionDetails()
        );

        ComplianceCheckId checkId = initiateCheckUseCase.initiateCheck(command);
        ComplianceCheck check = getCheckUseCase.getById(checkId.getValue(), tenantId);

        return ResponseEntity.ok(ComplianceCheckResponse.from(check));
    }

    /**
     * Get compliance check by ID
     */
    @GetMapping("/{checkId}")
    @PreAuthorize("hasAnyAuthority('COMPLIANCE_OFFICER', 'SYSTEM_ADMIN', 'LC_OFFICER')")
    public ResponseEntity<ComplianceCheckResponse> getCheck(
            @PathVariable UUID checkId,
            @RequestHeader("X-Tenant-ID") UUID tenantId) {

        ComplianceCheck check = getCheckUseCase.getById(checkId, tenantId);
        return ResponseEntity.ok(ComplianceCheckResponse.from(check));
    }

    /**
     * Get compliance checks for an entity
     */
    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasAnyAuthority('COMPLIANCE_OFFICER', 'SYSTEM_ADMIN', 'LC_OFFICER')")
    public ResponseEntity<List<ComplianceCheckResponse>> getChecksByEntity(
            @PathVariable String entityType,
            @PathVariable UUID entityId,
            @RequestHeader("X-Tenant-ID") UUID tenantId) {

        List<ComplianceCheck> checks = getCheckUseCase.getByEntity(entityType, entityId, tenantId);
        List<ComplianceCheckResponse> response = checks.stream()
                .map(ComplianceCheckResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Get checks requiring manual review
     */
    @GetMapping("/review-required")
    @PreAuthorize("hasAnyAuthority('COMPLIANCE_OFFICER', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<ComplianceCheckResponse>> getChecksRequiringReview(
            @RequestHeader("X-Tenant-ID") UUID tenantId) {

        List<ComplianceCheck> checks = getCheckUseCase.getChecksRequiringReview(tenantId);
        List<ComplianceCheckResponse> response = checks.stream()
                .map(ComplianceCheckResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Approve a compliance check
     */
    @PostMapping("/{checkId}/approve")
    @PreAuthorize("hasAnyAuthority('COMPLIANCE_OFFICER', 'SYSTEM_ADMIN')")
    public ResponseEntity<Void> approveCheck(
            @PathVariable UUID checkId,
            @RequestBody ApproveCheckRequest request,
            @RequestHeader("X-Tenant-ID") UUID tenantId) {

        log.info("Approving compliance check via API: checkId={}", checkId);

        ApproveComplianceCheckCommand command = new ApproveComplianceCheckCommand(
                tenantId,
                checkId,
                request.getApprovedBy(),
                request.getReason()
        );

        approveCheckUseCase.approveCheck(command);
        return ResponseEntity.ok().build();
    }

    /**
     * Reject a compliance check
     */
    @PostMapping("/{checkId}/reject")
    @PreAuthorize("hasAnyAuthority('COMPLIANCE_OFFICER', 'SYSTEM_ADMIN')")
    public ResponseEntity<Void> rejectCheck(
            @PathVariable UUID checkId,
            @RequestBody RejectCheckRequest request,
            @RequestHeader("X-Tenant-ID") UUID tenantId) {

        log.info("Rejecting compliance check via API: checkId={}", checkId);

        RejectComplianceCheckCommand command = new RejectComplianceCheckCommand(
                tenantId,
                checkId,
                request.getRejectedBy(),
                request.getReason()
        );

        rejectCheckUseCase.rejectCheck(command);
        return ResponseEntity.ok().build();
    }

    /**
     * Clear as false positive
     */
    @PostMapping("/{checkId}/false-positive")
    @PreAuthorize("hasAnyAuthority('COMPLIANCE_OFFICER', 'SYSTEM_ADMIN')")
    public ResponseEntity<Void> clearAsFalsePositive(
            @PathVariable UUID checkId,
            @RequestBody ClearFalsePositiveRequest request,
            @RequestHeader("X-Tenant-ID") UUID tenantId) {

        log.info("Clearing compliance check as false positive via API: checkId={}", checkId);

        ClearFalsePositiveCommand command = new ClearFalsePositiveCommand(
                tenantId,
                checkId,
                request.getClearedBy(),
                request.getReason()
        );

        clearFalsePositiveUseCase.clearAsFalsePositive(command);
        return ResponseEntity.ok().build();
    }

    /**
     * Check if entity has passed compliance
     */
    @GetMapping("/entity/{entityType}/{entityId}/passed")
    @PreAuthorize("hasAnyAuthority('COMPLIANCE_OFFICER', 'SYSTEM_ADMIN', 'LC_OFFICER')")
    public ResponseEntity<ComplianceStatusResponse> checkComplianceStatus(
            @PathVariable String entityType,
            @PathVariable UUID entityId,
            @RequestHeader("X-Tenant-ID") UUID tenantId) {

        boolean passed = getCheckUseCase.hasPassedCompliance(entityType, entityId, tenantId);
        return ResponseEntity.ok(new ComplianceStatusResponse(passed));
    }
}

