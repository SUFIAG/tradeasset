package com.tradesphere.ai.customer.adapter.in.web.mapper;

import com.tradesphere.ai.customer.adapter.in.web.dto.*;
import com.tradesphere.ai.customer.application.port.in.*;
import com.tradesphere.ai.customer.domain.model.Customer;
import com.tradesphere.ai.shared.domain.valueobject.Address;
import com.tradesphere.ai.shared.domain.valueobject.Money;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mapper for converting between REST DTOs and Application Commands/Domain Models
 */
@Component
public class CustomerDTOMapper {

    public CreateCustomerCommand toCommand(CreateCustomerRequest request, UUID tenantId, UUID createdBy) {
        CreateCustomerCommand.CreateCustomerCommandBuilder builder = CreateCustomerCommand.builder()
                .tenantId(tenantId)
                .name(request.getName())
                .legalName(request.getLegalName())
                .type(request.getType())
                .category(request.getCategory())
                .industry(request.getIndustry())
                .businessRegistrationNumber(request.getBusinessRegistrationNumber())
                .taxId(request.getTaxId())
                .email(request.getEmail())
                .phone(request.getPhone())
                .website(request.getWebsite())
                .createdBy(createdBy);

        // Map address if provided
        if (request.getAddressLine1() != null) {
            Address address = Address.of(
                    request.getAddressLine1(),
                    request.getAddressLine2(),
                    request.getCity(),
                    request.getState(),
                    request.getPostalCode(),
                    request.getCountry()
            );
            builder.address(address);
        }

        return builder.build();
    }

    public UpdateCustomerCommand toCommand(String customerId, UpdateCustomerRequest request, UUID updatedBy) {
        UpdateCustomerCommand.UpdateCustomerCommandBuilder builder = UpdateCustomerCommand.builder()
                .customerId(customerId)
                .name(request.getName())
                .legalName(request.getLegalName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .website(request.getWebsite())
                .updatedBy(updatedBy);

        // Map address if provided
        if (request.getAddressLine1() != null) {
            Address address = Address.of(
                    request.getAddressLine1(),
                    request.getAddressLine2(),
                    request.getCity(),
                    request.getState(),
                    request.getPostalCode(),
                    request.getCountry()
            );
            builder.address(address);
        }

        return builder.build();
    }

    public SetCreditLimitCommand toCommand(String customerId, SetCreditLimitRequest request, UUID approvedBy) {
        return SetCreditLimitCommand.builder()
                .customerId(customerId)
                .creditLimit(Money.of(request.getAmount(), request.getCurrency()))
                .approvedBy(approvedBy)
                .build();
    }

    public UpdateRiskRatingCommand toCommand(String customerId, UpdateRiskRatingRequest request, UUID updatedBy) {
        return UpdateRiskRatingCommand.builder()
                .customerId(customerId)
                .riskRating(request.getRiskRating())
                .riskScore(request.getRiskScore())
                .reason(request.getReason())
                .updatedBy(updatedBy)
                .build();
    }

    public CompleteKYCCommand toCommand(String customerId, CompleteKYCRequest request, UUID completedBy) {
        return CompleteKYCCommand.builder()
                .customerId(customerId)
                .expiryDate(request.getExpiryDate())
                .completedBy(completedBy)
                .build();
    }

    public CustomerResponse toResponse(Customer customer) {
        CustomerResponse.CustomerResponseBuilder builder = CustomerResponse.builder()
                .id(customer.getId().toString())
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
                .updatedAt(customer.getUpdatedAt());

        // Map Address
        if (customer.getAddress() != null) {
            Address address = customer.getAddress();
            builder.address(CustomerResponse.AddressDto.builder()
                    .line1(address.getLine1())
                    .line2(address.getLine2())
                    .city(address.getCity())
                    .state(address.getState())
                    .postalCode(address.getPostalCode())
                    .country(address.getCountry())
                    .build());
        }

        // Map Credit Limit
        if (customer.getCreditLimit() != null) {
            builder.creditLimit(CustomerResponse.MoneyDto.builder()
                    .amount(customer.getCreditLimit().getAmount())
                    .currency(customer.getCreditLimit().getCurrency().getCurrencyCode())
                    .build());
        }

        // Map Available Credit
        if (customer.getAvailableCredit() != null) {
            builder.availableCredit(CustomerResponse.MoneyDto.builder()
                    .amount(customer.getAvailableCredit().getAmount())
                    .currency(customer.getAvailableCredit().getCurrency().getCurrencyCode())
                    .build());
        }

        return builder.build();
    }
}

