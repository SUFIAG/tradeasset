package com.tradesphere.ai.customer.domain.model;

import com.tradesphere.ai.shared.domain.AggregateRoot;
import com.tradesphere.ai.shared.domain.valueobject.Address;
import com.tradesphere.ai.shared.domain.valueobject.Money;
import com.tradesphere.ai.customer.domain.event.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Customer Aggregate Root
 *
 * Represents a customer in the trade finance ecosystem (importer, exporter, bank, etc.)
 * This is the aggregate root for all customer-related data and operations
 *
 * Business Rules Enforced:
 * - Customer must have required KYC documents before approval
 * - Compliance must be cleared before approval
 * - Risk rating must be assessed before setting credit limit
 * - Customer can only transact when ACTIVE
 */
@Getter
public class Customer extends AggregateRoot<CustomerId> {

    // Identity
    private UUID tenantId;
    private String customerNumber;  // Business identifier (e.g., CUST-2026-00001)

    // Basic Information
    private String name;
    private String legalName;
    private CustomerType type;
    private String category;  // INDIVIDUAL, CORPORATE, GOVERNMENT, SME, LARGE_ENTERPRISE

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
    private Address address;

    // KYC (Know Your Customer)
    private KYCStatus kycStatus;
    private Instant kycCompletedAt;
    private LocalDate kycExpiryDate;
    private LocalDate kycNextReviewDate;

    // Compliance
    private ComplianceStatus complianceStatus;
    private Instant lastComplianceCheckAt;
    private Instant nextComplianceCheckAt;
    private boolean pepStatus;  // Politically Exposed Person
    private boolean sanctionsHit;
    private boolean highRiskCountry;

    // Risk Assessment
    private RiskRating riskRating;
    private BigDecimal riskScore;  // 0.00 to 100.00
    private Instant riskAssessedAt;
    private LocalDate riskNextReviewDate;

    // Credit Management
    private Money creditLimit;
    private Money availableCredit;
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

    /**
     * Private constructor - use factory methods to create Customer
     */
    private Customer(CustomerId id, UUID tenantId) {
        super(id);
        this.tenantId = tenantId;
        this.status = CustomerStatus.DRAFT;
        this.kycStatus = KYCStatus.PENDING;
        this.complianceStatus = ComplianceStatus.PENDING;
        this.riskRating = RiskRating.NOT_ASSESSED;
        this.pepStatus = false;
        this.sanctionsHit = false;
        this.highRiskCountry = false;
    }

    /**
     * Reconstruction constructor for persistence layer
     * Used to reconstruct aggregate from database
     */
    private Customer(CustomerId id) {
        super(id);
        this.tenantId = null; // Will be set by builder
    }

    /**
     * Builder for reconstructing Customer from persistence
     * Use this ONLY for persistence layer reconstruction
     */
    public static CustomerBuilder builder() {
        return new CustomerBuilder();
    }

    public static class CustomerBuilder {
        private Customer customer;

        public CustomerBuilder() {
            // Empty builder
        }

        public CustomerBuilder id(UUID id) {
            this.customer = new Customer(CustomerId.of(id));
            return this;
        }

        public CustomerBuilder tenantId(UUID tenantId) {
            customer.tenantId = tenantId;
            return this;
        }

        public CustomerBuilder customerNumber(String customerNumber) {
            customer.customerNumber = customerNumber;
            return this;
        }

        public CustomerBuilder name(String name) {
            customer.name = name;
            return this;
        }

        public CustomerBuilder legalName(String legalName) {
            customer.legalName = legalName;
            return this;
        }

        public CustomerBuilder type(CustomerType type) {
            customer.type = type;
            return this;
        }

        public CustomerBuilder category(String category) {
            customer.category = category;
            return this;
        }

        public CustomerBuilder industry(String industry) {
            customer.industry = industry;
            return this;
        }

        public CustomerBuilder businessRegistrationNumber(String businessRegistrationNumber) {
            customer.businessRegistrationNumber = businessRegistrationNumber;
            return this;
        }

        public CustomerBuilder taxId(String taxId) {
            customer.taxId = taxId;
            return this;
        }

        public CustomerBuilder incorporationDate(LocalDate incorporationDate) {
            customer.incorporationDate = incorporationDate;
            return this;
        }

        public CustomerBuilder incorporationCountry(String incorporationCountry) {
            customer.incorporationCountry = incorporationCountry;
            return this;
        }

        public CustomerBuilder email(String email) {
            customer.email = email;
            return this;
        }

        public CustomerBuilder phone(String phone) {
            customer.phone = phone;
            return this;
        }

        public CustomerBuilder website(String website) {
            customer.website = website;
            return this;
        }

        public CustomerBuilder address(Address address) {
            customer.address = address;
            return this;
        }

        public CustomerBuilder kycStatus(KYCStatus kycStatus) {
            customer.kycStatus = kycStatus;
            return this;
        }

        public CustomerBuilder kycCompletedAt(Instant kycCompletedAt) {
            customer.kycCompletedAt = kycCompletedAt;
            return this;
        }

        public CustomerBuilder kycExpiryDate(LocalDate kycExpiryDate) {
            customer.kycExpiryDate = kycExpiryDate;
            return this;
        }

        public CustomerBuilder kycNextReviewDate(LocalDate kycNextReviewDate) {
            customer.kycNextReviewDate = kycNextReviewDate;
            return this;
        }

        public CustomerBuilder complianceStatus(ComplianceStatus complianceStatus) {
            customer.complianceStatus = complianceStatus;
            return this;
        }

        public CustomerBuilder lastComplianceCheckAt(Instant lastComplianceCheckAt) {
            customer.lastComplianceCheckAt = lastComplianceCheckAt;
            return this;
        }

        public CustomerBuilder nextComplianceCheckAt(Instant nextComplianceCheckAt) {
            customer.nextComplianceCheckAt = nextComplianceCheckAt;
            return this;
        }

        public CustomerBuilder pepStatus(boolean pepStatus) {
            customer.pepStatus = pepStatus;
            return this;
        }

        public CustomerBuilder sanctionsHit(boolean sanctionsHit) {
            customer.sanctionsHit = sanctionsHit;
            return this;
        }

        public CustomerBuilder highRiskCountry(boolean highRiskCountry) {
            customer.highRiskCountry = highRiskCountry;
            return this;
        }

        public CustomerBuilder riskRating(RiskRating riskRating) {
            customer.riskRating = riskRating;
            return this;
        }

        public CustomerBuilder riskScore(BigDecimal riskScore) {
            customer.riskScore = riskScore;
            return this;
        }

        public CustomerBuilder riskAssessedAt(Instant riskAssessedAt) {
            customer.riskAssessedAt = riskAssessedAt;
            return this;
        }

        public CustomerBuilder riskNextReviewDate(LocalDate riskNextReviewDate) {
            customer.riskNextReviewDate = riskNextReviewDate;
            return this;
        }

        public CustomerBuilder creditLimit(Money creditLimit) {
            customer.creditLimit = creditLimit;
            return this;
        }

        public CustomerBuilder availableCredit(Money availableCredit) {
            customer.availableCredit = availableCredit;
            return this;
        }

        public CustomerBuilder creditApprovedBy(UUID creditApprovedBy) {
            customer.creditApprovedBy = creditApprovedBy;
            return this;
        }

        public CustomerBuilder creditApprovedAt(Instant creditApprovedAt) {
            customer.creditApprovedAt = creditApprovedAt;
            return this;
        }

        public CustomerBuilder relationshipManagerId(UUID relationshipManagerId) {
            customer.relationshipManagerId = relationshipManagerId;
            return this;
        }

        public CustomerBuilder customerSince(LocalDate customerSince) {
            customer.customerSince = customerSince;
            return this;
        }

        public CustomerBuilder status(CustomerStatus status) {
            customer.status = status;
            return this;
        }

        public CustomerBuilder approvedBy(UUID approvedBy) {
            customer.approvedBy = approvedBy;
            return this;
        }

        public CustomerBuilder approvedAt(Instant approvedAt) {
            customer.approvedAt = approvedAt;
            return this;
        }

        public CustomerBuilder notes(String notes) {
            customer.notes = notes;
            return this;
        }

        public CustomerBuilder createdBy(UUID createdBy) {
            customer.createdBy = createdBy;
            return this;
        }

        public CustomerBuilder createdAt(Instant createdAt) {
            customer.createdAt = createdAt;
            return this;
        }

        public CustomerBuilder updatedBy(UUID updatedBy) {
            customer.updatedBy = updatedBy;
            return this;
        }

        public CustomerBuilder updatedAt(Instant updatedAt) {
            customer.updatedAt = updatedAt;
            return this;
        }

        public CustomerBuilder version(Long version) {
            customer.version = version;
            return this;
        }

        public Customer build() {
            return customer;
        }
    }

    /**
     * Factory method to create a new Customer
     *
     * @param tenantId The tenant this customer belongs to
     * @param name Customer name
     * @param legalName Legal name of the customer
     * @param type Type of customer (IMPORTER, EXPORTER, etc.)
     * @param email Contact email
     * @param createdBy User creating this customer
     * @return New Customer instance
     */
    public static Customer create(
            UUID tenantId,
            String name,
            String legalName,
            CustomerType type,
            String email,
            UUID createdBy) {

        // Validate inputs
        validateNotNull(tenantId, "Tenant ID");
        validateNotEmpty(name, "Customer name");
        validateNotEmpty(legalName, "Legal name");
        validateNotNull(type, "Customer type");
        validateEmail(email);
        validateNotNull(createdBy, "Created by user");

        // Create customer
        Customer customer = new Customer(CustomerId.generate(), tenantId);
        customer.name = name;
        customer.legalName = legalName;
        customer.type = type;
        customer.email = email;
        customer.createdBy = createdBy;
        customer.createdAt = Instant.now();
        customer.customerSince = LocalDate.now();

        // Generate customer number (in real implementation, this would be from a sequence)
        customer.customerNumber = generateCustomerNumber();

        // Raise domain event
        customer.registerEvent(CustomerCreated.of(
            customer.getId().toString(),
            customer.customerNumber,
            name,
            type.name(),
            createdBy
        ));

        return customer;
    }

    /**
     * Update basic customer information
     */
    public void updateBasicInfo(
            String name,
            String legalName,
            String email,
            String phone,
            UUID updatedBy) {

        validateNotEmpty(name, "Customer name");
        validateNotEmpty(legalName, "Legal name");
        validateEmail(email);

        this.name = name;
        this.legalName = legalName;
        this.email = email;
        this.phone = phone;
        markAsUpdated(updatedBy);
    }

    /**
     * Set customer address
     */
    public void setAddress(Address address) {
        if (address == null) {
            throw new CustomerException("Address cannot be null");
        }
        this.address = address;
    }

    /**
     * Submit customer for approval
     * Business Rule: Can only submit DRAFT customers with completed KYC
     */
    public void submitForApproval(UUID submittedBy) {
        if (status != CustomerStatus.DRAFT) {
            throw CustomerException.invalidStatus(status, "submit");
        }

        if (kycStatus != KYCStatus.COMPLETED) {
            throw CustomerException.kycNotCompleted();
        }

        this.status = CustomerStatus.PENDING_APPROVAL;
        markAsUpdated(submittedBy);

        registerEvent(CustomerSubmittedForApproval.of(
            getId().toString(),
            customerNumber,
            submittedBy
        ));
    }

    /**
     * Approve customer
     * Business Rule: Can only approve PENDING_APPROVAL customers with cleared compliance
     */
    public void approve(UUID approvedBy) {
        if (status != CustomerStatus.PENDING_APPROVAL) {
            throw CustomerException.invalidStatus(status, "approve");
        }

        if (kycStatus != KYCStatus.COMPLETED) {
            throw CustomerException.kycNotCompleted();
        }

        if (complianceStatus != ComplianceStatus.CLEARED) {
            throw CustomerException.complianceNotCleared();
        }

        this.status = CustomerStatus.APPROVED;
        this.approvedBy = approvedBy;
        this.approvedAt = Instant.now();
        markAsUpdated(approvedBy);

        registerEvent(CustomerApproved.of(
            getId().toString(),
            customerNumber,
            approvedBy
        ));
    }

    /**
     * Activate customer - makes them able to transact
     */
    public void activate(UUID activatedBy) {
        if (status != CustomerStatus.APPROVED) {
            throw CustomerException.invalidStatus(status, "activate");
        }

        this.status = CustomerStatus.ACTIVE;
        markAsUpdated(activatedBy);

        registerEvent(CustomerActivated.of(
            getId().toString(),
            customerNumber,
            activatedBy
        ));
    }

    /**
     * Reject customer application
     */
    public void reject(String reason, UUID rejectedBy) {
        if (status != CustomerStatus.PENDING_APPROVAL) {
            throw CustomerException.invalidStatus(status, "reject");
        }

        this.status = CustomerStatus.REJECTED;
        this.notes = reason;
        markAsUpdated(rejectedBy);

        registerEvent(CustomerRejected.of(
            getId().toString(),
            customerNumber,
            reason,
            rejectedBy
        ));
    }

    /**
     * Suspend customer
     * Business Rule: Can only suspend ACTIVE customers
     */
    public void suspend(String reason, UUID suspendedBy) {
        if (status != CustomerStatus.ACTIVE) {
            throw CustomerException.invalidStatus(status, "suspend");
        }

        this.status = CustomerStatus.SUSPENDED;
        this.notes = reason;
        markAsUpdated(suspendedBy);

        registerEvent(CustomerSuspended.of(
            getId().toString(),
            customerNumber,
            reason,
            suspendedBy
        ));
    }

    /**
     * Reactivate suspended customer
     */
    public void reactivate(UUID reactivatedBy) {
        if (!status.canBeReactivated()) {
            throw CustomerException.invalidStatus(status, "reactivate");
        }

        this.status = CustomerStatus.ACTIVE;
        markAsUpdated(reactivatedBy);

        registerEvent(CustomerReactivated.of(
            getId().toString(),
            customerNumber,
            reactivatedBy
        ));
    }

    /**
     * Complete KYC process
     */
    public void completeKYC(LocalDate expiryDate, UUID completedBy) {
        this.kycStatus = KYCStatus.COMPLETED;
        this.kycCompletedAt = Instant.now();
        this.kycExpiryDate = expiryDate;
        this.kycNextReviewDate = expiryDate.minusMonths(1); // Review 1 month before expiry
        markAsUpdated(completedBy);

        registerEvent(CustomerKYCCompleted.of(
            getId().toString(),
            customerNumber,
            completedBy
        ));
    }

    /**
     * Update risk rating
     * Business Rule: Risk score must be between 0 and 100
     */
    public void updateRiskRating(RiskRating newRating, BigDecimal newScore, String reason, UUID updatedBy) {
        if (newScore != null && (newScore.compareTo(BigDecimal.ZERO) < 0 || newScore.compareTo(BigDecimal.valueOf(100)) > 0)) {
            throw new CustomerException("Risk score must be between 0 and 100");
        }

        RiskRating oldRating = this.riskRating;

        this.riskRating = newRating;
        this.riskScore = newScore;
        this.riskAssessedAt = Instant.now();
        this.riskNextReviewDate = LocalDate.now().plusMonths(6); // Review every 6 months
        markAsUpdated(updatedBy);

        registerEvent(CustomerRiskRatingChanged.of(
            getId().toString(),
            customerNumber,
            oldRating.name(),
            newRating.name(),
            newScore,
            reason,
            updatedBy
        ));
    }

    /**
     * Set credit limit
     * Business Rule: Credit limit cannot be negative
     * Business Rule: Risk must be assessed before setting credit limit
     */
    public void setCreditLimit(Money limit, UUID approvedBy) {
        if (limit == null || limit.isNegative()) {
            throw new CustomerException("Credit limit cannot be null or negative");
        }

        if (riskRating == RiskRating.NOT_ASSESSED) {
            throw new CustomerException("Risk must be assessed before setting credit limit");
        }

        this.creditLimit = limit;
        this.availableCredit = limit; // Initially, full limit is available
        this.creditApprovedBy = approvedBy;
        this.creditApprovedAt = Instant.now();
        markAsUpdated(approvedBy);

        registerEvent(CustomerCreditLimitSet.of(
            getId().toString(),
            customerNumber,
            limit,
            approvedBy
        ));
    }

    /**
     * Reserve credit (reduce available credit)
     * Used when creating LC or other transactions
     */
    public void reserveCredit(Money amount) {
        if (availableCredit == null) {
            throw new CustomerException("No credit limit set for customer");
        }

        if (amount.isGreaterThan(availableCredit)) {
            throw CustomerException.creditLimitExceeded(customerNumber);
        }

        this.availableCredit = availableCredit.subtract(amount);
    }

    /**
     * Release credit (increase available credit)
     * Used when LC is closed or transaction is cancelled
     */
    public void releaseCredit(Money amount) {
        if (availableCredit == null) {
            throw new CustomerException("No credit limit set for customer");
        }

        this.availableCredit = availableCredit.add(amount);

        // Ensure available credit doesn't exceed credit limit
        if (availableCredit.isGreaterThan(creditLimit)) {
            this.availableCredit = creditLimit;
        }
    }

    /**
     * Update compliance status
     */
    public void updateComplianceStatus(ComplianceStatus newStatus, UUID updatedBy) {
        this.complianceStatus = newStatus;
        this.lastComplianceCheckAt = Instant.now();
        this.nextComplianceCheckAt = Instant.now().plusSeconds(30L * 24 * 60 * 60); // Next check in 30 days
        markAsUpdated(updatedBy);

        registerEvent(CustomerComplianceStatusChanged.of(
            getId().toString(),
            customerNumber,
            newStatus.name(),
            updatedBy
        ));
    }

    /**
     * Mark as PEP (Politically Exposed Person)
     */
    public void markAsPEP(UUID updatedBy) {
        this.pepStatus = true;
        // PEP customers automatically require enhanced due diligence
        if (riskRating == RiskRating.LOW || riskRating == RiskRating.MEDIUM) {
            updateRiskRating(RiskRating.HIGH, BigDecimal.valueOf(75), "Customer is a PEP", updatedBy);
        }
        markAsUpdated(updatedBy);
    }

    /**
     * Mark sanctions hit
     */
    public void markSanctionsHit(UUID updatedBy) {
        this.sanctionsHit = true;
        this.complianceStatus = ComplianceStatus.FLAGGED;
        markAsUpdated(updatedBy);
    }

    /**
     * Assign relationship manager
     */
    public void assignRelationshipManager(UUID managerId) {
        this.relationshipManagerId = managerId;
    }

    // Validation helpers

    private static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new CustomerException(fieldName + " cannot be null");
        }
    }

    private static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new CustomerException(fieldName + " cannot be empty");
        }
    }

    private static void validateEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new CustomerException("Invalid email format");
        }
    }

    private static String generateCustomerNumber() {
        // In real implementation, this would come from a sequence service
        // Format: CUST-YYYY-NNNNN
        return String.format("CUST-%d-%05d",
            LocalDate.now().getYear(),
            (int)(Math.random() * 100000));
    }
}

