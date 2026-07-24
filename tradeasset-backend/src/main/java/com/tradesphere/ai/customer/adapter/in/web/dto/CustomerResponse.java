package com.tradesphere.ai.customer.adapter.in.web.dto;

import com.tradesphere.ai.customer.domain.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * REST DTO for customer response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private String id;
    private UUID tenantId;
    private String customerNumber;

    // Basic Information
    private String name;
    private String legalName;
    private CustomerType type;
    private String category;

    // Business Details
    private String industry;
    private String businessRegistrationNumber;
    private String taxId;
    private LocalDate incorporationDate;
    private String incorporationCountry;

    // Contact Information
    private String email;
    private String phone;
    private String website;

    // Address
    private AddressDto address;

    // KYC
    private KYCStatus kycStatus;
    private Instant kycCompletedAt;
    private LocalDate kycExpiryDate;
    private LocalDate kycNextReviewDate;

    // Compliance
    private ComplianceStatus complianceStatus;
    private Instant lastComplianceCheckAt;
    private Instant nextComplianceCheckAt;
    private boolean pepStatus;
    private boolean sanctionsHit;
    private boolean highRiskCountry;

    // Risk Assessment
    private RiskRating riskRating;
    private BigDecimal riskScore;
    private Instant riskAssessedAt;
    private LocalDate riskNextReviewDate;

    // Credit Management
    private MoneyDto creditLimit;
    private MoneyDto availableCredit;
    private UUID creditApprovedBy;
    private Instant creditApprovedAt;

    // Relationship Management
    private UUID relationshipManagerId;
    private LocalDate customerSince;

    // Status
    private CustomerStatus status;
    private UUID approvedBy;
    private Instant approvedAt;

    // Notes
    private String notes;

    // Audit
    private UUID createdBy;
    private Instant createdAt;
    private UUID updatedBy;
    private Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDto {
        private String line1;
        private String line2;
        private String city;
        private String state;
        private String postalCode;
        private String country;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MoneyDto {
        private BigDecimal amount;
        private String currency;
    }
}

