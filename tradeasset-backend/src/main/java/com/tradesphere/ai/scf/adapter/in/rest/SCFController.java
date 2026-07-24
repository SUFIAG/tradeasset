package com.tradesphere.ai.scf.adapter.in.rest;

import com.tradesphere.ai.scf.application.port.in.*;
import com.tradesphere.ai.scf.application.port.in.dto.*;
import com.tradesphere.ai.scf.domain.model.SCFStatus;
import com.tradesphere.ai.scf.domain.model.SCFType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Supply Chain Finance operations
 */
@RestController
@RequestMapping("/api/v1/scf")
@RequiredArgsConstructor
@Slf4j
public class SCFController {

    private final CreateSCFUseCase createSCFUseCase;
    private final ApproveSCFUseCase approveSCFUseCase;
    private final DisburseSCFUseCase disburseSCFUseCase;
    private final RepaySCFUseCase repaySCFUseCase;
    private final GetSCFUseCase getSCFUseCase;
    private final AcceptEarlyPaymentUseCase acceptEarlyPaymentUseCase;
    private final BatchSCFUseCase batchSCFUseCase;

    /**
     * Create new SCF transaction
     */
    @PostMapping
    public ResponseEntity<SCFResponse> createSCF(
            @RequestBody CreateSCFCommand command,
            @RequestHeader("X-Tenant-Id") String tenantId) {

        log.info("Creating SCF for tenant={}, type={}", tenantId, command.getType());
        command.setTenantId(UUID.fromString(tenantId));

        SCFResponse response = createSCFUseCase.createSCF(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Approve SCF financing
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<SCFResponse> approveSCF(
            @PathVariable UUID id,
            @RequestBody ApproveSCFCommand command,
            @RequestHeader("X-Tenant-Id") String tenantId) {

        log.info("Approving SCF: id={}, tenant={}", id, tenantId);
        command.setScfId(id);
        command.setTenantId(tenantId);

        SCFResponse response = approveSCFUseCase.approveSCF(command);
        return ResponseEntity.ok(response);
    }

    /**
     * Disburse SCF funds
     */
    @PostMapping("/{id}/disburse")
    public ResponseEntity<SCFResponse> disburseSCF(
            @PathVariable UUID id,
            @RequestBody DisburseSCFCommand command,
            @RequestHeader("X-Tenant-Id") String tenantId) {

        log.info("Disbursing SCF: id={}, tenant={}", id, tenantId);
        command.setScfId(id);
        command.setTenantId(tenantId);

        SCFResponse response = disburseSCFUseCase.disburseSCF(command);
        return ResponseEntity.ok(response);
    }

    /**
     * Repay SCF financing
     */
    @PostMapping("/{id}/repay")
    public ResponseEntity<SCFResponse> repaySCF(
            @PathVariable UUID id,
            @RequestBody RepaySCFCommand command,
            @RequestHeader("X-Tenant-Id") String tenantId) {

        log.info("Repaying SCF: id={}, tenant={}", id, tenantId);
        command.setScfId(id);
        command.setTenantId(tenantId);

        SCFResponse response = repaySCFUseCase.repaySCF(command);
        return ResponseEntity.ok(response);
    }

    /**
     * Get SCF by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SCFResponse> getSCFById(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-Id") String tenantId) {

        log.info("Getting SCF: id={}, tenant={}", id, tenantId);
        SCFResponse response = getSCFUseCase.getSCFById(id, tenantId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get SCF by reference
     */
    @GetMapping("/reference/{reference}")
    public ResponseEntity<SCFResponse> getSCFByReference(
            @PathVariable String reference,
            @RequestHeader("X-Tenant-Id") String tenantId) {

        log.info("Getting SCF by reference: reference={}, tenant={}", reference, tenantId);
        SCFResponse response = getSCFUseCase.getSCFByReference(reference, tenantId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all SCF for tenant
     */
    @GetMapping
    public ResponseEntity<List<SCFResponse>> getAllSCF(
            @RequestHeader("X-Tenant-Id") String tenantId) {

        log.info("Getting all SCF for tenant={}", tenantId);
        List<SCFResponse> response = getSCFUseCase.getAllSCF(tenantId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get SCF by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<SCFResponse>> getSCFByStatus(
            @PathVariable SCFStatus status,
            @RequestHeader("X-Tenant-Id") String tenantId) {

        log.info("Getting SCF by status: status={}, tenant={}", status, tenantId);
        List<SCFResponse> response = getSCFUseCase.getSCFByStatus(tenantId, status);
        return ResponseEntity.ok(response);
    }

    /**
     * Get SCF by type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<SCFResponse>> getSCFByType(
            @PathVariable SCFType type,
            @RequestHeader("X-Tenant-Id") String tenantId) {

        log.info("Getting SCF by type: type={}, tenant={}", type, tenantId);
        List<SCFResponse> response = getSCFUseCase.getSCFByType(tenantId, type);
        return ResponseEntity.ok(response);
    }

    /**
     * Get SCF by supplier
     */
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<SCFResponse>> getSCFBySupplier(
            @PathVariable UUID supplierId,
            @RequestHeader("X-Tenant-Id") String tenantId) {

        log.info("Getting SCF by supplier: supplierId={}, tenant={}", supplierId, tenantId);
        List<SCFResponse> response = getSCFUseCase.getSCFBySupplier(supplierId, tenantId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get SCF by buyer
     */
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<SCFResponse>> getSCFByBuyer(
            @PathVariable UUID buyerId,
            @RequestHeader("X-Tenant-Id") String tenantId) {

        log.info("Getting SCF by buyer: buyerId={}, tenant={}", buyerId, tenantId);
        List<SCFResponse> response = getSCFUseCase.getSCFByBuyer(buyerId, tenantId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get SCF by financier
     */
    @GetMapping("/financier/{financierId}")
    public ResponseEntity<List<SCFResponse>> getSCFByFinancier(
            @PathVariable UUID financierId,
            @RequestHeader("X-Tenant-Id") String tenantId) {

        log.info("Getting SCF by financier: financierId={}, tenant={}", financierId, tenantId);
        List<SCFResponse> response = getSCFUseCase.getSCFByFinancier(financierId, tenantId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get SCF due soon
     */
    @GetMapping("/due-soon")
    public ResponseEntity<List<SCFResponse>> getSCFDueSoon(
            @RequestParam(defaultValue = "7") int days,
            @RequestHeader("X-Tenant-Id") String tenantId) {

        log.info("Getting SCF due soon: days={}, tenant={}", days, tenantId);
        List<SCFResponse> response = getSCFUseCase.getSCFDueSoon(tenantId, days);
        return ResponseEntity.ok(response);
    }

    /**
     * Get overdue SCF
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<SCFResponse>> getSCFOverdue(
            @RequestHeader("X-Tenant-Id") String tenantId) {

        log.info("Getting overdue SCF for tenant={}", tenantId);
        List<SCFResponse> response = getSCFUseCase.getSCFOverdue(tenantId);
        return ResponseEntity.ok(response);
    }

    /**
     * Accept early payment offer
     */
    @PostMapping("/{id}/early-payment")
    public ResponseEntity<SCFResponse> acceptEarlyPayment(
            @PathVariable UUID id,
            @RequestBody AcceptEarlyPaymentCommand command,
            @RequestHeader("X-Tenant-Id") String tenantId) {

        log.info("Accepting early payment: id={}, tenant={}", id, tenantId);
        command.setScfId(id);
        command.setTenantId(tenantId);

        SCFResponse response = acceptEarlyPaymentUseCase.acceptEarlyPayment(command);
        return ResponseEntity.ok(response);
    }

    /**
     * Process batch SCF operations
     */
    @PostMapping("/batch")
    public ResponseEntity<BatchSCFResponse> processBatch(
            @RequestBody BatchSCFCommand command,
            @RequestHeader("X-Tenant-Id") String tenantId) {

        log.info("Processing batch operation: type={}, count={}, tenant={}",
            command.getOperationType(), command.getScfIds().size(), tenantId);
        command.setTenantId(UUID.fromString(tenantId));

        BatchSCFResponse response = batchSCFUseCase.processBatch(command);
        return ResponseEntity.ok(response);
    }

    /**
     * Exception handler
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleException(RuntimeException e) {
        log.error("Error processing SCF request", e);
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            e.getMessage(),
            System.currentTimeMillis()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Error response DTO
     */
    public record ErrorResponse(int status, String message, long timestamp) {}
}

