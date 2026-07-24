package com.tradesphere.ai.risk.adapter.in.web;

import com.tradesphere.ai.risk.application.port.in.*;
import com.tradesphere.ai.risk.domain.model.RiskAssessment;
import com.tradesphere.ai.risk.domain.model.RiskAssessmentId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST API for risk assessments
 */
@RestController
@RequestMapping("/api/risk/assessments")
@RequiredArgsConstructor
@Slf4j
public class RiskAssessmentController {

    private final InitiateRiskAssessmentUseCase initiateUseCase;
    private final GetRiskAssessmentUseCase getUseCase;

    /**
     * Initiate a new risk assessment
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('RISK_OFFICER', 'SYSTEM_ADMIN')")
    public ResponseEntity<RiskAssessmentResponse> initiateAssessment(
            @RequestBody InitiateRiskAssessmentRequest request,
            @RequestHeader("X-Tenant-ID") UUID tenantId) {

        log.info("Initiating risk assessment via API: entityType={}, entityId={}",
                request.getEntityType(), request.getEntityId());

        InitiateRiskAssessmentCommand command = new InitiateRiskAssessmentCommand(
                tenantId,
                request.getRiskType(),
                request.getEntityType(),
                request.getEntityId(),
                request.getEntityName(),
                request.getInitiatedBy(),
                request.getCountryCode(),
                request.getTransactionAmount(),
                request.getCustomerType()
        );

        RiskAssessmentId assessmentId = initiateUseCase.initiateAssessment(command);
        RiskAssessment assessment = getUseCase.getById(assessmentId.getValue(), tenantId);

        return ResponseEntity.ok(RiskAssessmentResponse.from(assessment));
    }

    /**
     * Get risk assessment by ID
     */
    @GetMapping("/{assessmentId}")
    @PreAuthorize("hasAnyAuthority('RISK_OFFICER', 'SYSTEM_ADMIN', 'LC_OFFICER')")
    public ResponseEntity<RiskAssessmentResponse> getAssessment(
            @PathVariable UUID assessmentId,
            @RequestHeader("X-Tenant-ID") UUID tenantId) {

        RiskAssessment assessment = getUseCase.getById(assessmentId, tenantId);
        return ResponseEntity.ok(RiskAssessmentResponse.from(assessment));
    }

    /**
     * Get risk assessments for an entity
     */
    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasAnyAuthority('RISK_OFFICER', 'SYSTEM_ADMIN', 'LC_OFFICER')")
    public ResponseEntity<List<RiskAssessmentResponse>> getAssessmentsByEntity(
            @PathVariable String entityType,
            @PathVariable UUID entityId,
            @RequestHeader("X-Tenant-ID") UUID tenantId) {

        List<RiskAssessment> assessments = getUseCase.getByEntity(entityType, entityId, tenantId);
        List<RiskAssessmentResponse> response = assessments.stream()
                .map(RiskAssessmentResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Get high risk assessments requiring review
     */
    @GetMapping("/high-risk")
    @PreAuthorize("hasAnyAuthority('RISK_OFFICER', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<RiskAssessmentResponse>> getHighRiskAssessments(
            @RequestHeader("X-Tenant-ID") UUID tenantId) {

        List<RiskAssessment> assessments = getUseCase.getHighRiskAssessments(tenantId);
        List<RiskAssessmentResponse> response = assessments.stream()
                .map(RiskAssessmentResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}

