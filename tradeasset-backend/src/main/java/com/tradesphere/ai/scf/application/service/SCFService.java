package com.tradesphere.ai.scf.application.service;

import com.tradesphere.ai.scf.application.port.in.*;
import com.tradesphere.ai.scf.application.port.in.dto.*;
import com.tradesphere.ai.scf.application.port.out.ExternalFinancingPlatform;
import com.tradesphere.ai.scf.application.port.out.SCFRepository;
import com.tradesphere.ai.scf.domain.model.SCFStatus;
import com.tradesphere.ai.scf.domain.model.SCFType;
import com.tradesphere.ai.scf.domain.model.SupplyChainFinance;
import com.tradesphere.ai.scf.domain.service.DiscountCalculationEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Enhanced Service implementation for Supply Chain Finance operations
 * Now includes: Early payment, Batch processing, Discount engine, External platform integration
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SCFService implements
    CreateSCFUseCase,
    ApproveSCFUseCase,
    DisburseSCFUseCase,
    RepaySCFUseCase,
    GetSCFUseCase,
    AcceptEarlyPaymentUseCase,
    BatchSCFUseCase {

    private final SCFRepository scfRepository;
    private final DiscountCalculationEngine discountEngine;
    private final ExternalFinancingPlatform externalPlatform;

    @Override
    public SCFResponse createSCF(CreateSCFCommand command) {
        log.info("Creating SCF for supplier={}, buyer={}, amount={}",
            command.getSupplierName(), command.getBuyerName(), command.getInvoiceAmount());

        SupplyChainFinance scf = SupplyChainFinance.create(
            command.getTenantId(),
            command.getType(),
            command.getSupplierId(),
            command.getSupplierName(),
            command.getBuyerId(),
            command.getBuyerName(),
            command.getInvoiceAmount(),
            command.getAdvanceRate(),
            command.getCurrency(),
            command.getDueDate(),
            command.getCreatedBy()
        );

        // Set optional fields
        setFieldIfNotNull(scf, "invoiceNumber", command.getInvoiceNumber());
        setFieldIfNotNull(scf, "purchaseOrderNumber", command.getPurchaseOrderNumber());
        setFieldIfNotNull(scf, "invoiceDate", command.getInvoiceDate());
        setFieldIfNotNull(scf, "paymentTermDays", command.getPaymentTermDays());
        setFieldIfNotNull(scf, "goodsDescription", command.getGoodsDescription());
        setFieldIfNotNull(scf, "shipmentReference", command.getShipmentReference());

        SupplyChainFinance saved = scfRepository.save(scf);
        log.info("SCF created successfully: id={}, reference={}", saved.getId(), saved.getScfReference());

        return toResponse(saved);
    }

    @Override
    public SCFResponse approveSCF(ApproveSCFCommand command) {
        log.info("Approving SCF: id={}", command.getScfId());

        SupplyChainFinance scf = scfRepository.findById(command.getScfId(), command.getTenantId())
            .orElseThrow(() -> new RuntimeException("SCF not found: " + command.getScfId()));

        // Set financier
        setFieldIfNotNull(scf, "financierId", command.getFinancierId());
        setFieldIfNotNull(scf, "financierName", command.getFinancierName());

        scf.approve(command.getDiscountRate(), command.getApprovedBy());

        SupplyChainFinance saved = scfRepository.save(scf);
        log.info("SCF approved successfully: id={}, discountRate={}", saved.getId(), command.getDiscountRate());

        return toResponse(saved);
    }

    @Override
    public SCFResponse disburseSCF(DisburseSCFCommand command) {
        log.info("Disbursing SCF: id={}", command.getScfId());

        SupplyChainFinance scf = scfRepository.findById(command.getScfId(), command.getTenantId())
            .orElseThrow(() -> new RuntimeException("SCF not found: " + command.getScfId()));

        scf.disburse();

        SupplyChainFinance saved = scfRepository.save(scf);
        log.info("SCF disbursed successfully: id={}, financeAmount={}", saved.getId(), saved.getFinanceAmount());

        return toResponse(saved);
    }

    @Override
    public SCFResponse repaySCF(RepaySCFCommand command) {
        log.info("Repaying SCF: id={}, amount={}", command.getScfId(), command.getAmount());

        SupplyChainFinance scf = scfRepository.findById(command.getScfId(), command.getTenantId())
            .orElseThrow(() -> new RuntimeException("SCF not found: " + command.getScfId()));

        scf.repay(command.getAmount(), command.getRepaymentDate());

        SupplyChainFinance saved = scfRepository.save(scf);
        log.info("SCF repaid successfully: id={}", saved.getId());

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SCFResponse getSCFById(UUID id, String tenantId) {
        return scfRepository.findById(id, tenantId)
            .map(this::toResponse)
            .orElseThrow(() -> new RuntimeException("SCF not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public SCFResponse getSCFByReference(String reference, String tenantId) {
        return scfRepository.findByReference(reference, tenantId)
            .map(this::toResponse)
            .orElseThrow(() -> new RuntimeException("SCF not found: " + reference));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SCFResponse> getAllSCF(String tenantId) {
        return scfRepository.findByTenantId(tenantId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SCFResponse> getSCFByStatus(String tenantId, SCFStatus status) {
        return scfRepository.findByStatus(tenantId, status).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SCFResponse> getSCFByType(String tenantId, SCFType type) {
        return scfRepository.findByType(tenantId, type).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SCFResponse> getSCFBySupplier(UUID supplierId, String tenantId) {
        return scfRepository.findBySupplier(supplierId, tenantId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SCFResponse> getSCFByBuyer(UUID buyerId, String tenantId) {
        return scfRepository.findByBuyer(buyerId, tenantId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SCFResponse> getSCFByFinancier(UUID financierId, String tenantId) {
        return scfRepository.findByFinancier(financierId, tenantId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SCFResponse> getSCFDueSoon(String tenantId, int days) {
        return scfRepository.findDueSoon(tenantId, days).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SCFResponse> getSCFOverdue(String tenantId) {
        return scfRepository.findOverdue(tenantId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public SCFResponse acceptEarlyPayment(AcceptEarlyPaymentCommand command) {
        log.info("Accepting early payment: scfId={}, earlyDate={}",
            command.getScfId(), command.getEarlyPaymentDate());

        SupplyChainFinance scf = scfRepository.findById(command.getScfId(), command.getTenantId())
            .orElseThrow(() -> new RuntimeException("SCF not found: " + command.getScfId()));

        scf.offerEarlyPayment(command.getDiscountPercent(), command.getEarlyPaymentDate());

        // Recalculate with early payment discount
        if (scf.getDiscountRate() != null) {
            DiscountCalculationEngine.DiscountRateRequest request = new DiscountCalculationEngine.DiscountRateRequest();
            request.setInvoiceAmount(scf.getInvoiceAmount());
            request.setFinanceAmount(scf.getFinanceAmount());
            request.setDueDate(scf.getDueDate());
            request.setPaymentTermDays(scf.getPaymentTermDays());
            request.setEarlyPaymentDays(
                (int) java.time.temporal.ChronoUnit.DAYS.between(
                    command.getEarlyPaymentDate(), scf.getDueDate()
                )
            );

            DiscountCalculationEngine.DiscountRateResult result = discountEngine.calculateDiscountRate(request);
            setFieldIfNotNull(scf, "earlyPaymentDiscount", result.getEarlyPaymentDiscount());
        }

        SupplyChainFinance saved = scfRepository.save(scf);
        log.info("Early payment accepted: scfId={}, discount={}",
            saved.getId(), saved.getEarlyPaymentDiscount());

        return toResponse(saved);
    }

    @Override
    public BatchSCFResponse processBatch(BatchSCFCommand command) {
        log.info("Processing batch SCF operation: type={}, count={}",
            command.getOperationType(), command.getScfIds().size());

        List<BatchSCFResponse.BatchResult> results = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (UUID scfId : command.getScfIds()) {
            try {
                boolean success = processSingleBatchOperation(
                    scfId,
                    command.getTenantId().toString(),
                    command.getOperationType(),
                    command.getProcessedBy()
                );

                if (success) {
                    successCount++;
                    results.add(BatchSCFResponse.BatchResult.builder()
                        .scfId(scfId)
                        .success(true)
                        .message("Operation completed successfully")
                        .build());
                } else {
                    failureCount++;
                    results.add(BatchSCFResponse.BatchResult.builder()
                        .scfId(scfId)
                        .success(false)
                        .message("Operation failed")
                        .errorCode("OPERATION_FAILED")
                        .build());
                }
            } catch (Exception e) {
                failureCount++;
                log.error("Batch operation failed for SCF: {}", scfId, e);
                results.add(BatchSCFResponse.BatchResult.builder()
                    .scfId(scfId)
                    .success(false)
                    .message(e.getMessage())
                    .errorCode("EXCEPTION")
                    .build());
            }
        }

        log.info("Batch processing complete: success={}, failure={}", successCount, failureCount);

        return BatchSCFResponse.builder()
            .batchReference(command.getBatchReference())
            .operationType(command.getOperationType())
            .totalCount(command.getScfIds().size())
            .successCount(successCount)
            .failureCount(failureCount)
            .results(results)
            .processedAt(LocalDateTime.now())
            .build();
    }

    /**
     * Process single batch operation
     */
    private boolean processSingleBatchOperation(UUID scfId, String tenantId,
            BatchSCFCommand.BatchOperationType operationType, UUID processedBy) {

        SupplyChainFinance scf = scfRepository.findById(scfId, tenantId)
            .orElseThrow(() -> new RuntimeException("SCF not found: " + scfId));

        switch (operationType) {
            case APPROVE -> {
                if (scf.getStatus() == SCFStatus.PENDING) {
                    // Use discount engine for automatic approval
                    DiscountCalculationEngine.DiscountRateRequest request = buildDiscountRequest(scf);
                    DiscountCalculationEngine.DiscountRateResult result = discountEngine.calculateDiscountRate(request);
                    scf.approve(result.getDiscountRate(), processedBy);
                    scfRepository.save(scf);
                    return true;
                }
                return false;
            }
            case DISBURSE -> {
                if (scf.getStatus() == SCFStatus.APPROVED && !scf.isDisbursed()) {
                    scf.disburse();
                    scfRepository.save(scf);
                    return true;
                }
                return false;
            }
            case REPAY -> {
                if (scf.isDisbursed() && !scf.isRepaid()) {
                    scf.repay(scf.getFinanceAmount(), java.time.LocalDate.now());
                    scfRepository.save(scf);
                    return true;
                }
                return false;
            }
            case CANCEL -> {
                if (scf.getStatus() == SCFStatus.PENDING) {
                    setFieldIfNotNull(scf, "status", SCFStatus.CANCELLED);
                    scfRepository.save(scf);
                    return true;
                }
                return false;
            }
            default -> {
                return false;
            }
        }
    }

    /**
     * Build discount request from SCF
     */
    private DiscountCalculationEngine.DiscountRateRequest buildDiscountRequest(SupplyChainFinance scf) {
        DiscountCalculationEngine.DiscountRateRequest request = new DiscountCalculationEngine.DiscountRateRequest();
        request.setSupplierName(scf.getSupplierName());
        request.setInvoiceAmount(scf.getInvoiceAmount());
        request.setFinanceAmount(scf.getFinanceAmount());
        request.setRiskRating(scf.getRiskRating());
        request.setCreditScore(scf.getCreditScore());
        request.setPaymentTermDays(scf.getPaymentTermDays());
        request.setDueDate(scf.getDueDate());
        return request;
    }

    private SCFResponse toResponse(SupplyChainFinance scf) {
        return SCFResponse.builder()
            .id(scf.getId().getValue())
            .tenantId(scf.getTenantId())
            .scfReference(scf.getScfReference())
            .type(scf.getType())
            .status(scf.getStatus())
            .supplierId(scf.getSupplierId())
            .supplierName(scf.getSupplierName())
            .buyerId(scf.getBuyerId())
            .buyerName(scf.getBuyerName())
            .financierId(scf.getFinancierId())
            .financierName(scf.getFinancierName())
            .invoiceAmount(scf.getInvoiceAmount())
            .financeAmount(scf.getFinanceAmount())
            .advanceRate(scf.getAdvanceRate())
            .currency(scf.getCurrency())
            .discountRate(scf.getDiscountRate())
            .feeAmount(scf.getFeeAmount())
            .interestRate(scf.getInterestRate())
            .netAmount(scf.getNetAmount())
            .invoiceNumber(scf.getInvoiceNumber())
            .purchaseOrderNumber(scf.getPurchaseOrderNumber())
            .invoiceDate(scf.getInvoiceDate())
            .dueDate(scf.getDueDate())
            .paymentTermDays(scf.getPaymentTermDays())
            .financeDate(scf.getFinanceDate())
            .maturityDate(scf.getMaturityDate())
            .earlyPaymentDate(scf.getEarlyPaymentDate())
            .earlyPaymentDiscount(scf.getEarlyPaymentDiscount())
            .approved(scf.isApproved())
            .approvedAt(scf.getApprovedAt())
            .disbursed(scf.isDisbursed())
            .disbursedAt(scf.getDisbursedAt())
            .repaid(scf.isRepaid())
            .repaidAt(scf.getRepaidAt())
            .goodsDescription(scf.getGoodsDescription())
            .shipmentReference(scf.getShipmentReference())
            .creditScore(scf.getCreditScore())
            .riskRating(scf.getRiskRating())
            .creditLimit(scf.getCreditLimit())
            .createdAt(java.time.LocalDateTime.ofInstant(scf.getCreatedAt(), java.time.ZoneId.systemDefault()))
            .createdBy(scf.getCreatedBy())
            .updatedAt(scf.getUpdatedAt() != null ?
                java.time.LocalDateTime.ofInstant(scf.getUpdatedAt(), java.time.ZoneId.systemDefault()) : null)
            .updatedBy(scf.getUpdatedBy())
            .build();
    }

    private void setFieldIfNotNull(Object target, String fieldName, Object value) {
        if (value != null) {
            try {
                java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
            } catch (Exception e) {
                log.warn("Failed to set field {}: {}", fieldName, e.getMessage());
            }
        }
    }
}

