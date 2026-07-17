package com.tradesphere.ai.scf.adapter.out.persistence;

import com.tradesphere.ai.scf.domain.model.SCFId;
import com.tradesphere.ai.scf.domain.model.SupplyChainFinance;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mapper between Domain Model and JPA Entity for SCF
 */
@Component
public class SCFMapper {

    public SCFJpaEntity toEntity(SupplyChainFinance domain) {
        if (domain == null) return null;

        SCFJpaEntity entity = new SCFJpaEntity();
        entity.setId(domain.getId().getValue());
        entity.setTenantId(domain.getTenantId().toString());
        entity.setScfReference(domain.getScfReference());
        entity.setType(domain.getType());
        entity.setStatus(domain.getStatus());

        // Parties
        entity.setSupplierId(domain.getSupplierId());
        entity.setSupplierName(domain.getSupplierName());
        entity.setBuyerId(domain.getBuyerId());
        entity.setBuyerName(domain.getBuyerName());
        entity.setFinancierId(domain.getFinancierId());
        entity.setFinancierName(domain.getFinancierName());

        // Financial Details
        entity.setInvoiceAmount(domain.getInvoiceAmount());
        entity.setFinanceAmount(domain.getFinanceAmount());
        entity.setAdvanceRate(domain.getAdvanceRate());
        entity.setCurrency(domain.getCurrency());

        // Rates and Fees
        entity.setDiscountRate(domain.getDiscountRate());
        entity.setFeeAmount(domain.getFeeAmount());
        entity.setInterestRate(domain.getInterestRate());

        // Invoice/PO Details
        entity.setInvoiceNumber(domain.getInvoiceNumber());
        entity.setPurchaseOrderNumber(domain.getPurchaseOrderNumber());
        entity.setInvoiceDate(domain.getInvoiceDate());
        entity.setDueDate(domain.getDueDate());
        entity.setPaymentTermDays(domain.getPaymentTermDays());

        // Financing Details
        entity.setFinanceDate(domain.getFinanceDate());
        entity.setMaturityDate(domain.getMaturityDate());
        entity.setEarlyPaymentDate(domain.getEarlyPaymentDate());
        entity.setEarlyPaymentDiscount(domain.getEarlyPaymentDiscount());

        // Status Tracking
        entity.setApproved(domain.isApproved());
        entity.setApprovedAt(domain.getApprovedAt());
        entity.setDisbursed(domain.isDisbursed());
        entity.setDisbursedAt(domain.getDisbursedAt());
        entity.setRepaid(domain.isRepaid());
        entity.setRepaidAt(domain.getRepaidAt());

        // Goods Information
        entity.setGoodsDescription(domain.getGoodsDescription());
        entity.setShipmentReference(domain.getShipmentReference());

        // Risk Assessment
        entity.setCreditScore(domain.getCreditScore());
        entity.setRiskRating(domain.getRiskRating());
        entity.setCreditLimit(domain.getCreditLimit());

        // Workflow & Audit
        entity.setWorkflowId(domain.getWorkflowId());
        entity.setCreatedAt(java.time.LocalDateTime.ofInstant(domain.getCreatedAt(), java.time.ZoneId.systemDefault()));
        entity.setCreatedBy(domain.getCreatedBy() != null ? domain.getCreatedBy().toString() : null);
        entity.setUpdatedAt(domain.getUpdatedAt() != null ?
            java.time.LocalDateTime.ofInstant(domain.getUpdatedAt(), java.time.ZoneId.systemDefault()) : null);
        entity.setUpdatedBy(domain.getUpdatedBy() != null ? domain.getUpdatedBy().toString() : null);
        entity.setVersion(domain.getVersion());

        return entity;
    }

    public SupplyChainFinance toDomain(SCFJpaEntity entity) {
        if (entity == null) return null;

        SupplyChainFinance domain = SupplyChainFinance.create(
            UUID.fromString(entity.getTenantId()),
            entity.getType(),
            entity.getSupplierId(),
            entity.getSupplierName(),
            entity.getBuyerId(),
            entity.getBuyerName(),
            entity.getInvoiceAmount(),
            entity.getAdvanceRate(),
            entity.getCurrency(),
            entity.getDueDate(),
            entity.getCreatedBy() != null ? UUID.fromString(entity.getCreatedBy()) : null
        );

        // Use reflection to set the ID and other fields
        setField(domain, "id", new SCFId(entity.getId()));
        setField(domain, "scfReference", entity.getScfReference());
        setField(domain, "status", entity.getStatus());
        setField(domain, "financierId", entity.getFinancierId());
        setField(domain, "financierName", entity.getFinancierName());
        setField(domain, "financeAmount", entity.getFinanceAmount());
        setField(domain, "discountRate", entity.getDiscountRate());
        setField(domain, "feeAmount", entity.getFeeAmount());
        setField(domain, "interestRate", entity.getInterestRate());
        setField(domain, "invoiceNumber", entity.getInvoiceNumber());
        setField(domain, "purchaseOrderNumber", entity.getPurchaseOrderNumber());
        setField(domain, "invoiceDate", entity.getInvoiceDate());
        setField(domain, "paymentTermDays", entity.getPaymentTermDays());
        setField(domain, "financeDate", entity.getFinanceDate());
        setField(domain, "maturityDate", entity.getMaturityDate());
        setField(domain, "earlyPaymentDate", entity.getEarlyPaymentDate());
        setField(domain, "earlyPaymentDiscount", entity.getEarlyPaymentDiscount());
        setField(domain, "approved", entity.isApproved());
        setField(domain, "approvedAt", entity.getApprovedAt());
        setField(domain, "disbursed", entity.isDisbursed());
        setField(domain, "disbursedAt", entity.getDisbursedAt());
        setField(domain, "repaid", entity.isRepaid());
        setField(domain, "repaidAt", entity.getRepaidAt());
        setField(domain, "goodsDescription", entity.getGoodsDescription());
        setField(domain, "shipmentReference", entity.getShipmentReference());
        setField(domain, "creditScore", entity.getCreditScore());
        setField(domain, "riskRating", entity.getRiskRating());
        setField(domain, "creditLimit", entity.getCreditLimit());
        setField(domain, "workflowId", entity.getWorkflowId());
        setField(domain, "version", Long.valueOf(entity.getVersion()));

        if (entity.getUpdatedAt() != null) {
            setField(domain, "updatedAt", entity.getUpdatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant());
        }
        if (entity.getUpdatedBy() != null) {
            setField(domain, "updatedBy", UUID.fromString(entity.getUpdatedBy()));
        }

        return domain;
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            // Field might not exist or be accessible, skip
        }
    }
}


