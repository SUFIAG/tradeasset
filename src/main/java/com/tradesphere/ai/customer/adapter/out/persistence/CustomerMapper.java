package com.tradesphere.ai.customer.adapter.out.persistence;

import com.tradesphere.ai.customer.domain.model.Customer;
import com.tradesphere.ai.customer.domain.model.CustomerId;
import com.tradesphere.ai.shared.domain.valueobject.Address;
import com.tradesphere.ai.shared.domain.valueobject.Money;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between Customer domain model and CustomerJpaEntity
 */
@Component
public class CustomerMapper {

    public CustomerJpaEntity toJpaEntity(Customer customer) {
        CustomerJpaEntity entity = CustomerJpaEntity.builder()
                .id(customer.getId().getValue())
                .tenantId(customer.getTenantId())
                .customerNumber(customer.getCustomerNumber())
                .name(customer.getName())
                .legalName(customer.getLegalName())
                .type(customer.getType())
                .category(customer.getCategory())
                .industry(customer.getIndustry())
                .businessRegistrationNumber(customer.getBusinessRegistrationNumber())
                .taxId(customer.getTaxId())
                .incorporationDate(customer.getIncorporationDate())
                .incorporationCountry(customer.getIncorporationCountry())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .website(customer.getWebsite())
                .kycStatus(customer.getKycStatus())
                .kycCompletedAt(customer.getKycCompletedAt())
                .kycExpiryDate(customer.getKycExpiryDate())
                .kycNextReviewDate(customer.getKycNextReviewDate())
                .complianceStatus(customer.getComplianceStatus())
                .lastComplianceCheckAt(customer.getLastComplianceCheckAt())
                .nextComplianceCheckAt(customer.getNextComplianceCheckAt())
                .pepStatus(customer.isPepStatus())
                .sanctionsHit(customer.isSanctionsHit())
                .highRiskCountry(customer.isHighRiskCountry())
                .riskRating(customer.getRiskRating())
                .riskScore(customer.getRiskScore())
                .riskAssessedAt(customer.getRiskAssessedAt())
                .riskNextReviewDate(customer.getRiskNextReviewDate())
                .creditApprovedBy(customer.getCreditApprovedBy())
                .creditApprovedAt(customer.getCreditApprovedAt())
                .relationshipManagerId(customer.getRelationshipManagerId())
                .customerSince(customer.getCustomerSince())
                .status(customer.getStatus())
                .approvedBy(customer.getApprovedBy())
                .approvedAt(customer.getApprovedAt())
                .notes(customer.getNotes())
                .createdBy(customer.getCreatedBy())
                .createdAt(customer.getCreatedAt())
                .updatedBy(customer.getUpdatedBy())
                .updatedAt(customer.getUpdatedAt())
                .version(customer.getVersion())
                .build();

        // Map Address
        if (customer.getAddress() != null) {
            Address address = customer.getAddress();
            entity.setAddressLine1(address.getLine1());
            entity.setAddressLine2(address.getLine2());
            entity.setCity(address.getCity());
            entity.setState(address.getState());
            entity.setPostalCode(address.getPostalCode());
            entity.setCountry(address.getCountry());
        }

        // Map Credit Limit
        if (customer.getCreditLimit() != null) {
            entity.setCreditLimitAmount(customer.getCreditLimit().getAmount());
            entity.setCreditLimitCurrency(customer.getCreditLimit().getCurrency().getCurrencyCode());
        }

        // Map Available Credit
        if (customer.getAvailableCredit() != null) {
            entity.setAvailableCreditAmount(customer.getAvailableCredit().getAmount());
            entity.setAvailableCreditCurrency(customer.getAvailableCredit().getCurrency().getCurrencyCode());
        }

        return entity;
    }

    public Customer toDomainModel(CustomerJpaEntity entity) {
        Customer.CustomerBuilder builder = Customer.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .customerNumber(entity.getCustomerNumber())
                .name(entity.getName())
                .legalName(entity.getLegalName())
                .type(entity.getType())
                .category(entity.getCategory())
                .industry(entity.getIndustry())
                .businessRegistrationNumber(entity.getBusinessRegistrationNumber())
                .taxId(entity.getTaxId())
                .incorporationDate(entity.getIncorporationDate())
                .incorporationCountry(entity.getIncorporationCountry())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .website(entity.getWebsite())
                .kycStatus(entity.getKycStatus())
                .kycCompletedAt(entity.getKycCompletedAt())
                .kycExpiryDate(entity.getKycExpiryDate())
                .kycNextReviewDate(entity.getKycNextReviewDate())
                .complianceStatus(entity.getComplianceStatus())
                .lastComplianceCheckAt(entity.getLastComplianceCheckAt())
                .nextComplianceCheckAt(entity.getNextComplianceCheckAt())
                .pepStatus(entity.getPepStatus())
                .sanctionsHit(entity.getSanctionsHit())
                .highRiskCountry(entity.getHighRiskCountry())
                .riskRating(entity.getRiskRating())
                .riskScore(entity.getRiskScore())
                .riskAssessedAt(entity.getRiskAssessedAt())
                .riskNextReviewDate(entity.getRiskNextReviewDate())
                .creditApprovedBy(entity.getCreditApprovedBy())
                .creditApprovedAt(entity.getCreditApprovedAt())
                .relationshipManagerId(entity.getRelationshipManagerId())
                .customerSince(entity.getCustomerSince())
                .status(entity.getStatus())
                .approvedBy(entity.getApprovedBy())
                .approvedAt(entity.getApprovedAt())
                .notes(entity.getNotes())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .version(entity.getVersion());

        // Reconstruct Address if present
        if (entity.getAddressLine1() != null) {
            Address address = Address.of(
                    entity.getAddressLine1(),
                    entity.getAddressLine2(),
                    entity.getCity(),
                    entity.getState(),
                    entity.getPostalCode(),
                    entity.getCountry()
            );
            builder.address(address);
        }

        // Reconstruct Credit Limit if present
        if (entity.getCreditLimitAmount() != null && entity.getCreditLimitCurrency() != null) {
            Money creditLimit = Money.of(entity.getCreditLimitAmount(), entity.getCreditLimitCurrency());
            builder.creditLimit(creditLimit);
        }

        // Reconstruct Available Credit if present
        if (entity.getAvailableCreditAmount() != null && entity.getAvailableCreditCurrency() != null) {
            Money availableCredit = Money.of(entity.getAvailableCreditAmount(), entity.getAvailableCreditCurrency());
            builder.availableCredit(availableCredit);
        }

        return builder.build();
    }
}


