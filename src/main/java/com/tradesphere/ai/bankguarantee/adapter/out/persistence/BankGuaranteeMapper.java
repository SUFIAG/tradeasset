package com.tradesphere.ai.bankguarantee.adapter.out.persistence;

import com.tradesphere.ai.bankguarantee.domain.model.BankGuarantee;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Mapper between Domain Model and JPA Entity
 */
@Component
public class BankGuaranteeMapper {

    public BankGuaranteeJpaEntity toEntity(BankGuarantee domain) {
        if (domain == null) return null;

        BankGuaranteeJpaEntity entity = new BankGuaranteeJpaEntity();
        entity.setId(domain.getId());
        entity.setTenantId(domain.getTenantId());
        entity.setBgNumber(domain.getBgNumber());
        entity.setReferenceNumber(domain.getReferenceNumber());
        entity.setApplicantId(domain.getApplicantId());
        entity.setBeneficiaryId(domain.getBeneficiaryId());
        entity.setIssuingBank(domain.getIssuingBank());
        entity.setAdvisingBank(domain.getAdvisingBank());
        entity.setBgType(domain.getBgType());
        entity.setAmount(domain.getAmount());
        entity.setCurrency(domain.getCurrency());
        entity.setApplicationDate(domain.getApplicationDate());
        entity.setIssueDate(domain.getIssueDate());
        entity.setEffectiveDate(domain.getEffectiveDate());
        entity.setExpiryDate(domain.getExpiryDate());
        entity.setClaimExpiryDate(domain.getClaimExpiryDate());
        entity.setStatus(domain.getStatus());
        entity.setPurpose(domain.getPurpose());
        entity.setGoverningLaw(domain.getGoverningLaw());
        entity.setJurisdiction(domain.getJurisdiction());
        entity.setClaimProcedure(domain.getClaimProcedure());
        entity.setUnderlyingContractNumber(domain.getUnderlyingContractNumber());
        entity.setUnderlyingContractDate(domain.getUnderlyingContractDate());
        entity.setUnderlyingContractValue(domain.getUnderlyingContractValue());
        entity.setReductionAllowed(domain.isReductionAllowed());
        entity.setExtensionAllowed(domain.isExtensionAllowed());
        entity.setReductionTerms(domain.getReductionTerms());
        entity.setExtensionTerms(domain.getExtensionTerms());
        entity.setClaimsCount(domain.getClaimsCount());
        entity.setClaimedAmount(domain.getClaimedAmount());
        entity.setPaidAmount(domain.getPaidAmount());

        if (domain.getRequiredDocuments() != null) {
            entity.setRequiredDocuments(domain.getRequiredDocuments().toArray(new String[0]));
        }

        entity.setSpecialConditions(domain.getSpecialConditions());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        entity.setApprovedAt(domain.getApprovedAt());
        entity.setApprovedBy(domain.getApprovedBy());
        entity.setIssuedAt(domain.getIssuedAt());
        entity.setIssuedBy(domain.getIssuedBy());
        entity.setVersion(domain.getVersion());

        return entity;
    }

    public BankGuarantee toDomain(BankGuaranteeJpaEntity entity) {
        if (entity == null) return null;

        BankGuarantee domain = new BankGuarantee();
        domain.setId(entity.getId());
        domain.setTenantId(entity.getTenantId());
        domain.setBgNumber(entity.getBgNumber());
        domain.setReferenceNumber(entity.getReferenceNumber());
        domain.setApplicantId(entity.getApplicantId());
        domain.setBeneficiaryId(entity.getBeneficiaryId());
        domain.setIssuingBank(entity.getIssuingBank());
        domain.setAdvisingBank(entity.getAdvisingBank());
        domain.setBgType(entity.getBgType());
        domain.setAmount(entity.getAmount());
        domain.setCurrency(entity.getCurrency());
        domain.setApplicationDate(entity.getApplicationDate());
        domain.setIssueDate(entity.getIssueDate());
        domain.setEffectiveDate(entity.getEffectiveDate());
        domain.setExpiryDate(entity.getExpiryDate());
        domain.setClaimExpiryDate(entity.getClaimExpiryDate());
        domain.setStatus(entity.getStatus());
        domain.setPurpose(entity.getPurpose());
        domain.setGoverningLaw(entity.getGoverningLaw());
        domain.setJurisdiction(entity.getJurisdiction());
        domain.setClaimProcedure(entity.getClaimProcedure());
        domain.setUnderlyingContractNumber(entity.getUnderlyingContractNumber());
        domain.setUnderlyingContractDate(entity.getUnderlyingContractDate());
        domain.setUnderlyingContractValue(entity.getUnderlyingContractValue());
        domain.setReductionAllowed(entity.isReductionAllowed());
        domain.setExtensionAllowed(entity.isExtensionAllowed());
        domain.setReductionTerms(entity.getReductionTerms());
        domain.setExtensionTerms(entity.getExtensionTerms());
        domain.setClaimsCount(entity.getClaimsCount());
        domain.setClaimedAmount(entity.getClaimedAmount());
        domain.setPaidAmount(entity.getPaidAmount());

        if (entity.getRequiredDocuments() != null) {
            domain.setRequiredDocuments(Arrays.asList(entity.getRequiredDocuments()));
        }

        domain.setSpecialConditions(entity.getSpecialConditions());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setCreatedBy(entity.getCreatedBy());
        domain.setUpdatedAt(entity.getUpdatedAt());
        domain.setUpdatedBy(entity.getUpdatedBy());
        domain.setApprovedAt(entity.getApprovedAt());
        domain.setApprovedBy(entity.getApprovedBy());
        domain.setIssuedAt(entity.getIssuedAt());
        domain.setIssuedBy(entity.getIssuedBy());
        domain.setVersion(entity.getVersion());

        return domain;
    }
}

