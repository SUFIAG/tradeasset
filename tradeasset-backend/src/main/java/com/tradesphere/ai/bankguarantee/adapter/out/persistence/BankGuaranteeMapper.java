package com.tradesphere.ai.bankguarantee.adapter.out.persistence;

import com.tradesphere.ai.bankguarantee.domain.model.BankGuarantee;
import com.tradesphere.ai.bankguarantee.domain.model.BankGuaranteeId;
import com.tradesphere.ai.bankguarantee.domain.model.BankGuaranteeStatus;
import com.tradesphere.ai.bankguarantee.domain.model.BGStatus;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.UUID;

/**
 * Mapper between Domain Model and JPA Entity using reflection to handle private fields
 */
@Component
public class BankGuaranteeMapper {

    public BankGuaranteeJpaEntity toEntity(BankGuarantee domain) {
        if (domain == null) return null;

        BankGuaranteeJpaEntity entity = new BankGuaranteeJpaEntity();
        entity.setId(domain.getId() != null ? domain.getId().getValue() : null);
        entity.setTenantId(domain.getTenantId() != null ? domain.getTenantId().toString() : null);
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
        entity.setIssueDate(domain.getIssueDate() != null ? domain.getIssueDate().atStartOfDay() : null);
        entity.setEffectiveDate(domain.getEffectiveDate() != null ? domain.getEffectiveDate().atStartOfDay() : null);
        entity.setExpiryDate(domain.getExpiryDate() != null ? domain.getExpiryDate().atStartOfDay() : null);
        entity.setClaimExpiryDate(domain.getClaimExpiryDate());
        entity.setStatus(mapStatusToBGStatus(domain.getStatus()));
        entity.setPurpose(domain.getPurpose());
        entity.setGoverningLaw(domain.getGoverningLaw());
        entity.setJurisdiction(domain.getJurisdiction());
        entity.setClaimProcedure(domain.getClaimProcedure());
        entity.setUnderlyingContractNumber(domain.getUnderlyingContractNumber());
        entity.setUnderlyingContractDate(domain.getUnderlyingContractDate() != null ? 
            domain.getUnderlyingContractDate().atZone(ZoneId.systemDefault()).toLocalDate() : null);
        entity.setUnderlyingContractValue(domain.getUnderlyingContractValue() != null ? 
            new java.math.BigDecimal(domain.getUnderlyingContractValue()) : null);
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
        entity.setCreatedAt(domain.getCreatedAt() != null ? 
            LocalDateTime.ofInstant(domain.getCreatedAt(), ZoneId.systemDefault()) : null);
        entity.setCreatedBy(domain.getCreatedBy() != null ? domain.getCreatedBy().toString() : null);
        entity.setUpdatedAt(domain.getUpdatedAt() != null ? 
            LocalDateTime.ofInstant(domain.getUpdatedAt(), ZoneId.systemDefault()) : null);
        entity.setUpdatedBy(domain.getUpdatedBy() != null ? domain.getUpdatedBy().toString() : null);
        entity.setApprovedAt(domain.getApprovedAt());
        entity.setApprovedBy(domain.getApprovedBy() != null ? domain.getApprovedBy().toString() : null);
        entity.setIssuedAt(domain.getIssuedAt());
        entity.setIssuedBy(domain.getIssuedBy() != null ? domain.getIssuedBy().toString() : null);
        entity.setVersion(domain.getVersion() != null ? domain.getVersion().intValue() : 0);

        return entity;
    }

    public BankGuarantee toDomain(BankGuaranteeJpaEntity entity) {
        if (entity == null) return null;

        try {
            // Use no-arg constructor
            BankGuarantee domain = new BankGuarantee();

            // Set ID using reflection
            setFieldValue(domain, "id", new BankGuaranteeId(entity.getId()));

            // Use public setters for remaining fields
            domain.setTenantId(entity.getTenantId() != null ? java.util.UUID.fromString(entity.getTenantId()) : null);
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
            domain.setIssueDate(entity.getIssueDate() != null ? entity.getIssueDate().toLocalDate() : null);
            domain.setEffectiveDate(entity.getEffectiveDate() != null ? entity.getEffectiveDate().toLocalDate() : null);
            domain.setExpiryDate(entity.getExpiryDate() != null ? entity.getExpiryDate().toLocalDate() : null);
            domain.setClaimExpiryDate(entity.getClaimExpiryDate());
            domain.setStatus(mapBGStatusToStatus(entity.getStatus()));
            domain.setPurpose(entity.getPurpose());
            domain.setGoverningLaw(entity.getGoverningLaw());
            domain.setJurisdiction(entity.getJurisdiction());
            domain.setClaimProcedure(entity.getClaimProcedure());
            domain.setUnderlyingContractNumber(entity.getUnderlyingContractNumber());
            domain.setUnderlyingContractDate(entity.getUnderlyingContractDate() != null ?
                entity.getUnderlyingContractDate().atStartOfDay() : null);
            domain.setUnderlyingContractValue(entity.getUnderlyingContractValue() != null ?
                entity.getUnderlyingContractValue().toString() : null);
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
            domain.setCreatedAt(entity.getCreatedAt() != null ?
                entity.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant() : null);
            domain.setCreatedBy(entity.getCreatedBy() != null ? java.util.UUID.fromString(entity.getCreatedBy()) : null);
            domain.setUpdatedAt(entity.getUpdatedAt() != null ?
                entity.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant() : null);
            domain.setUpdatedBy(entity.getUpdatedBy() != null ? java.util.UUID.fromString(entity.getUpdatedBy()) : null);
            domain.setApprovedAt(entity.getApprovedAt());
            domain.setApprovedBy(entity.getApprovedBy() != null ? java.util.UUID.fromString(entity.getApprovedBy()) : null);
            domain.setIssuedAt(entity.getIssuedAt());
            domain.setIssuedBy(entity.getIssuedBy() != null ? java.util.UUID.fromString(entity.getIssuedBy()) : null);
            domain.setVersion((long) entity.getVersion());

            return domain;
        } catch (Exception e) {
            throw new RuntimeException("Error mapping entity to domain: " + e.getMessage(), e);
        }
    }

    private void setFieldValue(Object target, String fieldName, Object value) throws Exception {
        Field field = findField(target.getClass(), fieldName);
        if (field != null) {
            field.setAccessible(true);
            field.set(target, value);
        }
    }

    private Field findField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    private BGStatus mapStatusToBGStatus(BankGuaranteeStatus status) {
        if (status == null) return null;
        return switch (status) {
            case DRAFT -> BGStatus.DRAFT;
            case PENDING_APPROVAL -> BGStatus.UNDER_REVIEW;
            case APPROVED -> BGStatus.APPROVED;
            case REJECTED -> BGStatus.REJECTED;
            case ACTIVE -> BGStatus.ACTIVE;
            case CLAIMED -> BGStatus.CLAIMED;
            case SETTLED -> BGStatus.FULLY_PAID;
            case EXPIRED -> BGStatus.EXPIRED;
            case CANCELLED -> BGStatus.CANCELLED;
            case INVOKED -> BGStatus.FULLY_PAID;
            case RELEASED -> BGStatus.RELEASED;
        };
    }

    private BankGuaranteeStatus mapBGStatusToStatus(BGStatus status) {
        if (status == null) return null;
        return switch (status) {
            case DRAFT -> BankGuaranteeStatus.DRAFT;
            case SUBMITTED, UNDER_REVIEW -> BankGuaranteeStatus.PENDING_APPROVAL;
            case APPROVED -> BankGuaranteeStatus.APPROVED;
            case REJECTED -> BankGuaranteeStatus.REJECTED;
            case ISSUED, ACTIVE -> BankGuaranteeStatus.ACTIVE;
            case CLAIMED -> BankGuaranteeStatus.CLAIMED;
            case PARTIALLY_PAID -> BankGuaranteeStatus.CLAIMED;
            case FULLY_PAID -> BankGuaranteeStatus.SETTLED;
            case EXPIRED -> BankGuaranteeStatus.EXPIRED;
            case CANCELLED -> BankGuaranteeStatus.CANCELLED;
            case RELEASED -> BankGuaranteeStatus.RELEASED;
        };
    }
}

