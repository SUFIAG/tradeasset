package com.tradesphere.ai.lc.adapter.out.persistence;

import com.tradesphere.ai.customer.domain.model.CustomerId;
import com.tradesphere.ai.lc.domain.model.LCId;
import com.tradesphere.ai.lc.domain.model.LCNumber;
import com.tradesphere.ai.lc.domain.model.LetterOfCredit;
import com.tradesphere.ai.shared.domain.valueobject.Money;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Mapper between LC domain model and JPA entity
 */
@Component
public class LCMapper {
    
    public LCJpaEntity toJpaEntity(LetterOfCredit lc) {
        LCJpaEntity entity = new LCJpaEntity();
        
        entity.setId(lc.getId().value());
        entity.setLcNumber(lc.getLcNumber() != null ? lc.getLcNumber().value() : null);
        entity.setLcType(lc.getLcType());
        entity.setStatus(lc.getStatus());
        entity.setTenantId(lc.getTenantId());
        
        entity.setApplicantId(lc.getApplicantId().getValue());
        entity.setApplicantName(lc.getApplicantName());
        entity.setApplicantAddress(lc.getApplicantAddress());
        
        entity.setBeneficiaryId(lc.getBeneficiaryId().getValue());
        entity.setBeneficiaryName(lc.getBeneficiaryName());
        entity.setBeneficiaryAddress(lc.getBeneficiaryAddress());
        
        entity.setIssuingBankId(lc.getIssuingBankId());
        entity.setAdvisingBankId(lc.getAdvisingBankId());
        entity.setConfirmingBankId(lc.getConfirmingBankId());
        
        entity.setLcAmountValue(lc.getLcAmount().getAmount());
        entity.setLcAmountCurrency(lc.getLcAmount().getCurrency().getCurrencyCode());
        entity.setTolerance(lc.getTolerance());
        
        if (lc.getAvailableAmount() != null) {
            entity.setAvailableAmountValue(lc.getAvailableAmount().getAmount());
            entity.setAvailableAmountCurrency(lc.getAvailableAmount().getCurrency().getCurrencyCode());
        }
        
        entity.setCurrency(lc.getCurrency());
        
        entity.setApplicationDate(lc.getApplicationDate());
        entity.setIssueDate(lc.getIssueDate());
        entity.setExpiryDate(lc.getExpiryDate());
        entity.setLastShipmentDate(lc.getLastShipmentDate());
        entity.setPresentationDate(lc.getPresentationDate());
        
        entity.setIncoterm(lc.getIncoterm());
        entity.setPortOfLoading(lc.getPortOfLoading());
        entity.setPortOfDischarge(lc.getPortOfDischarge());
        entity.setPlaceOfDestination(lc.getPlaceOfDestination());
        entity.setPartialShipmentAllowed(lc.isPartialShipmentAllowed());
        entity.setTransshipmentAllowed(lc.isTransshipmentAllowed());
        
        entity.setGoodsDescription(lc.getGoodsDescription());
        entity.setHsCode(lc.getHsCode());
        entity.setQuantity(lc.getQuantity());
        entity.setQuantityUnit(lc.getQuantityUnit());
        
        if (lc.getCreditLimitReserved() != null) {
            entity.setCreditLimitReservedValue(lc.getCreditLimitReserved().getAmount());
            entity.setCreditLimitReservedCurrency(lc.getCreditLimitReserved().getCurrency().getCurrencyCode());
        }
        entity.setCreditLimitApproved(lc.isCreditLimitApproved());
        
        entity.setDocumentsRequired(lc.getDocumentsRequired());
        entity.setCommercialInvoiceRequired(lc.isCommercialInvoiceRequired());
        entity.setBillOfLadingRequired(lc.isBillOfLadingRequired());
        entity.setPackingListRequired(lc.isPackingListRequired());
        entity.setCertificateOfOriginRequired(lc.isCertificateOfOriginRequired());
        entity.setInsuranceCertificateRequired(lc.isInsuranceCertificateRequired());
        
        entity.setSpecialInstructions(lc.getSpecialInstructions());
        entity.setAdditionalConditions(lc.getAdditionalConditions());
        
        entity.setSubmittedBy(lc.getSubmittedBy());
        entity.setSubmittedAt(lc.getSubmittedAt());
        entity.setApprovedBy(lc.getApprovedBy());
        entity.setApprovedAt(lc.getApprovedAt());
        entity.setRejectedBy(lc.getRejectedBy());
        entity.setRejectedAt(lc.getRejectedAt());
        entity.setRejectionReason(lc.getRejectionReason());
        
        entity.setSwiftMessage(lc.getSwiftMessage());
        entity.setSwiftSentAt(lc.getSwiftSentAt());
        
        entity.setComplianceCheckPassed(lc.isComplianceCheckPassed());
        entity.setSanctionsCheckPassed(lc.isSanctionsCheckPassed());
        entity.setRiskRating(lc.getRiskRating());
        
        entity.setVersion(lc.getVersion());
        entity.setCreatedBy(lc.getCreatedBy());
        entity.setCreatedAt(lc.getCreatedAt() != null ? LocalDateTime.ofInstant(lc.getCreatedAt(), ZoneOffset.UTC) : null);
        entity.setUpdatedBy(lc.getUpdatedBy());
        entity.setUpdatedAt(lc.getUpdatedAt() != null ? LocalDateTime.ofInstant(lc.getUpdatedAt(), ZoneOffset.UTC) : null);

        return entity;
    }
    
    public LetterOfCredit toDomainModel(LCJpaEntity entity) {
        return LetterOfCredit.builder()
                .id(LCId.from(entity.getId()))
                .lcNumber(entity.getLcNumber() != null ? LCNumber.from(entity.getLcNumber()) : null)
                .lcType(entity.getLcType())
                .status(entity.getStatus())
                .tenantId(entity.getTenantId())
                .applicantId(CustomerId.of(entity.getApplicantId()))
                .applicantName(entity.getApplicantName())
                .beneficiaryId(CustomerId.of(entity.getBeneficiaryId()))
                .beneficiaryName(entity.getBeneficiaryName())
                .lcAmount(Money.of(entity.getLcAmountValue(), entity.getLcAmountCurrency()))
                .expiryDate(entity.getExpiryDate())
                .createdBy(entity.getCreatedBy())
                .version(entity.getVersion())
                .build();
    }
}

