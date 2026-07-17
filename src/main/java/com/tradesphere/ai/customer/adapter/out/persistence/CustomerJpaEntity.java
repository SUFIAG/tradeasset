package com.tradesphere.ai.customer.adapter.out.persistence;

import com.tradesphere.ai.customer.domain.model.*;
import com.tradesphere.ai.shared.multitenancy.TenantEntityListener;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * JPA Entity for Customer
 * Maps domain Customer aggregate to database table
 */
@Entity
@Table(name = "customers", schema = "customer")
@EntityListeners(TenantEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerJpaEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "customer_number", nullable = false, unique = true, length = 50)
    private String customerNumber;

    // Basic Information
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "legal_name", nullable = false, length = 200)
    private String legalName;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private CustomerType type;

    @Column(name = "category", length = 50)
    private String category;

    // Business Details
    @Column(name = "industry", length = 100)
    private String industry;

    @Column(name = "business_registration_number", length = 100)
    private String businessRegistrationNumber;

    @Column(name = "tax_id", length = 100)
    private String taxId;

    @Column(name = "incorporation_date")
    private LocalDate incorporationDate;

    @Column(name = "incorporation_country", length = 3)
    private String incorporationCountry;

    // Contact Information
    @Column(name = "email", nullable = false, length = 200)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "website", length = 200)
    private String website;

    // Address (embedded)
    @Column(name = "address_line1", length = 200)
    private String addressLine1;

    @Column(name = "address_line2", length = 200)
    private String addressLine2;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "country", length = 3)
    private String country;

    // KYC
    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status", nullable = false, length = 50)
    private KYCStatus kycStatus;

    @Column(name = "kyc_completed_at")
    private Instant kycCompletedAt;

    @Column(name = "kyc_expiry_date")
    private LocalDate kycExpiryDate;

    @Column(name = "kyc_next_review_date")
    private LocalDate kycNextReviewDate;

    // Compliance
    @Enumerated(EnumType.STRING)
    @Column(name = "compliance_status", nullable = false, length = 50)
    private ComplianceStatus complianceStatus;

    @Column(name = "last_compliance_check_at")
    private Instant lastComplianceCheckAt;

    @Column(name = "next_compliance_check_at")
    private Instant nextComplianceCheckAt;

    @Column(name = "pep_status", nullable = false)
    private Boolean pepStatus;

    @Column(name = "sanctions_hit", nullable = false)
    private Boolean sanctionsHit;

    @Column(name = "high_risk_country", nullable = false)
    private Boolean highRiskCountry;

    // Risk Assessment
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_rating", nullable = false, length = 50)
    private RiskRating riskRating;

    @Column(name = "risk_score", precision = 5, scale = 2)
    private BigDecimal riskScore;

    @Column(name = "risk_assessed_at")
    private Instant riskAssessedAt;

    @Column(name = "risk_next_review_date")
    private LocalDate riskNextReviewDate;

    // Credit Management
    @Column(name = "credit_limit_amount", precision = 19, scale = 4)
    private BigDecimal creditLimitAmount;

    @Column(name = "credit_limit_currency", length = 3)
    private String creditLimitCurrency;

    @Column(name = "available_credit_amount", precision = 19, scale = 4)
    private BigDecimal availableCreditAmount;

    @Column(name = "available_credit_currency", length = 3)
    private String availableCreditCurrency;

    @Column(name = "credit_approved_by")
    private UUID creditApprovedBy;

    @Column(name = "credit_approved_at")
    private Instant creditApprovedAt;

    // Relationship Management
    @Column(name = "relationship_manager_id")
    private UUID relationshipManagerId;

    @Column(name = "customer_since")
    private LocalDate customerSince;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private CustomerStatus status;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    // Notes
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Audit fields
    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}

