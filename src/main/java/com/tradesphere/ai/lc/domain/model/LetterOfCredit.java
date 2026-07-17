package com.tradesphere.ai.lc.domain.model;

import com.tradesphere.ai.customer.domain.model.CustomerId;
import com.tradesphere.ai.lc.domain.event.*;
import com.tradesphere.ai.shared.domain.AggregateRoot;
import com.tradesphere.ai.shared.domain.valueobject.Money;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Letter of Credit Aggregate Root
 * Represents a Letter of Credit in the trade finance system
 *
 * Business Rules:
 * - LC must have valid applicant and beneficiary (cannot be same)
 * - LC amount must be positive and within customer credit limit
 * - Expiry date must be after shipment date
 * - LC can only be issued after approval
 * - Documents can only be reviewed after LC is issued
 * - Payment can only be made after documents are accepted or discrepancies waived
 */
@Getter
@Setter(AccessLevel.PRIVATE)
public class LetterOfCredit extends AggregateRoot<LCId> {

    // Basic Information
    private LCNumber lcNumber;
    private LCType lcType;
    private LCStatus status;

    // Parties
    private UUID tenantId;
    private CustomerId applicantId;        // The buyer/importer
    private String applicantName;
    private String applicantAddress;

    private CustomerId beneficiaryId;      // The seller/exporter
    private String beneficiaryName;
    private String beneficiaryAddress;

    private UUID issuingBankId;
    private UUID advisingBankId;
    private UUID confirmingBankId;         // Optional

    // Financial Terms
    private Money lcAmount;
    private BigDecimal tolerance;          // Percentage (e.g., 5%)
    private Money availableAmount;         // Remaining amount available
    private String currency;

    // Dates
    private LocalDate applicationDate;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private LocalDate lastShipmentDate;
    private LocalDate presentationDate;    // Latest date for document presentation

    // Trade Terms
    private Incoterm incoterm;
    private String portOfLoading;
    private String portOfDischarge;
    private String placeOfDestination;
    private boolean partialShipmentAllowed;
    private boolean transshipmentAllowed;

    // Goods Description
    private String goodsDescription;
    private String hsCode;                 // Harmonized System Code
    private BigDecimal quantity;
    private String quantityUnit;

    // Credit Terms
    private Money creditLimitReserved;
    private boolean creditLimitApproved;

    // Document Requirements
    private String documentsRequired;      // JSON or text description
    private boolean commercialInvoiceRequired;
    private boolean billOfLadingRequired;
    private boolean packingListRequired;
    private boolean certificateOfOriginRequired;
    private boolean insuranceCertificateRequired;

    // Special Instructions
    private String specialInstructions;
    private String additionalConditions;

    // Workflow
    private UUID submittedBy;
    private LocalDateTime submittedAt;
    private UUID approvedBy;
    private LocalDateTime approvedAt;
    private UUID rejectedBy;
    private LocalDateTime rejectedAt;
    private String rejectionReason;

    // SWIFT
    private String swiftMessage;           // MT700 message
    private LocalDateTime swiftSentAt;

    // Compliance & Risk
    private boolean complianceCheckPassed;
    private boolean sanctionsCheckPassed;
    private String riskRating;

    /**
     * Private constructor for aggregate reconstruction
     */
    private LetterOfCredit(LCId id) {
        super(id);
    }

    /**
     * Factory method to create a new LC application (Draft)
     */
    public static LetterOfCredit createNew(
            UUID tenantId,
            CustomerId applicantId,
            String applicantName,
            CustomerId beneficiaryId,
            String beneficiaryName,
            Money lcAmount,
            LCType lcType,
            LocalDate expiryDate,
            UUID createdBy) {

        // Validations
        validateParties(applicantId, beneficiaryId, applicantName, beneficiaryName);
        validateAmount(lcAmount);
        validateDates(null, expiryDate, null);

        LetterOfCredit lc = new LetterOfCredit(LCId.generate());
        lc.tenantId = tenantId;
        lc.applicantId = applicantId;
        lc.applicantName = applicantName;
        lc.beneficiaryId = beneficiaryId;
        lc.beneficiaryName = beneficiaryName;
        lc.lcAmount = lcAmount;
        lc.availableAmount = lcAmount;
        lc.currency = lcAmount.getCurrency().getCurrencyCode();
        lc.lcType = lcType;
        lc.expiryDate = expiryDate;
        lc.applicationDate = LocalDate.now();
        lc.status = LCStatus.DRAFT;
        lc.tolerance = BigDecimal.ZERO;
        lc.partialShipmentAllowed = false;
        lc.transshipmentAllowed = false;
        lc.commercialInvoiceRequired = true;
        lc.billOfLadingRequired = true;
        lc.setCreatedBy(createdBy);

        lc.registerEvent(new LCApplicationCreated(
                lc.getId().asString(),
                lc.applicantId.getValue().toString(),
                lc.beneficiaryId.getValue().toString(),
                lc.lcAmount,
                lc.expiryDate
        ));

        return lc;
    }

    /**
     * Update LC details (only in DRAFT status)
     */
    public void updateDetails(
            String goodsDescription,
            Incoterm incoterm,
            String portOfLoading,
            String portOfDischarge,
            LocalDate lastShipmentDate,
            UUID updatedBy) {

        if (status != LCStatus.DRAFT) {
            throw LCException.invalidStatus("LC can only be updated in DRAFT status. Current status: " + status);
        }

        this.goodsDescription = goodsDescription;
        this.incoterm = incoterm;
        this.portOfLoading = portOfLoading;
        this.portOfDischarge = portOfDischarge;
        this.lastShipmentDate = lastShipmentDate;
        this.setUpdatedBy(updatedBy);

        // Validate dates
        validateDates(lastShipmentDate, expiryDate, null);
    }

    /**
     * Submit LC for approval
     */
    public void submitForApproval(UUID submittedBy) {
        if (status != LCStatus.DRAFT) {
            throw LCException.invalidStatus("Only DRAFT LC can be submitted. Current status: " + status);
        }

        validateForSubmission();

        this.status = LCStatus.SUBMITTED;
        this.submittedBy = submittedBy;
        this.submittedAt = LocalDateTime.now();
        this.setUpdatedBy(submittedBy);

        registerEvent(new LCApplicationSubmitted(
                getId().asString(),
                applicantId.getValue().toString(),
                lcAmount
        ));
    }

    /**
     * Approve LC application
     */
    public void approve(UUID approvedBy, boolean creditLimitApproved) {
        if (status != LCStatus.SUBMITTED && status != LCStatus.UNDER_REVIEW
                && status != LCStatus.PENDING_CREDIT_CHECK) {
            throw LCException.invalidStatus("LC cannot be approved in status: " + status);
        }

        this.status = LCStatus.APPROVED;
        this.approvedBy = approvedBy;
        this.approvedAt = LocalDateTime.now();
        this.creditLimitApproved = creditLimitApproved;
        this.setUpdatedBy(approvedBy);

        registerEvent(new LCApplicationApproved(
                getId().asString(),
                applicantId.getValue().toString(),
                approvedBy.toString(),
                lcAmount
        ));
    }

    /**
     * Reject LC application
     */
    public void reject(UUID rejectedBy, String reason) {
        if (status == LCStatus.ISSUED || status == LCStatus.PAID || status == LCStatus.COMPLETED) {
            throw LCException.invalidStatus("LC cannot be rejected in status: " + status);
        }

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Rejection reason is required");
        }

        this.status = LCStatus.REJECTED;
        this.rejectedBy = rejectedBy;
        this.rejectedAt = LocalDateTime.now();
        this.rejectionReason = reason;
        this.setUpdatedBy(rejectedBy);

        registerEvent(new LCApplicationRejected(
                getId().asString(),
                applicantId.getValue().toString(),
                rejectedBy.toString(),
                reason
        ));
    }

    /**
     * Issue LC (generate LC number and SWIFT message)
     */
    public void issue(LCNumber lcNumber, UUID issuedBy) {
        if (status != LCStatus.APPROVED) {
            throw LCException.invalidStatus("Only APPROVED LC can be issued. Current status: " + status);
        }

        if (!creditLimitApproved) {
            throw LCException.creditLimitExceeded("Credit limit not approved for LC issuance");
        }

        this.lcNumber = lcNumber;
        this.status = LCStatus.ISSUED;
        this.issueDate = LocalDate.now();
        this.setUpdatedBy(issuedBy);

        registerEvent(new LCIssued(
                getId().asString(),
                lcNumber.value(),
                applicantId.getValue().toString(),
                beneficiaryId.getValue().toString(),
                lcAmount,
                expiryDate
        ));
    }

    /**
     * Request amendment to LC
     */
    public void requestAmendment(String amendmentDetails, UUID requestedBy) {
        if (status != LCStatus.ISSUED && status != LCStatus.AMENDED) {
            throw LCException.invalidStatus("Only ISSUED or AMENDED LC can be amended. Current status: " + status);
        }

        if (amendmentDetails == null || amendmentDetails.isBlank()) {
            throw new IllegalArgumentException("Amendment details are required");
        }

        this.status = LCStatus.AMENDMENT_REQUESTED;
        this.setUpdatedBy(requestedBy);

        registerEvent(new LCAmendmentRequested(
                getId().asString(),
                lcNumber.value(),
                amendmentDetails,
                requestedBy.toString()
        ));
    }

    /**
     * Approve amendment to LC
     */
    public void approveAmendment(
            Money newAmount,
            LocalDate newExpiryDate,
            LocalDate newLastShipmentDate,
            UUID approvedBy) {

        if (status != LCStatus.AMENDMENT_REQUESTED) {
            throw LCException.invalidStatus("Only AMENDMENT_REQUESTED LC can be approved. Current status: " + status);
        }

        // Update LC terms based on amendment
        if (newAmount != null && !newAmount.equals(this.lcAmount)) {
            validateAmount(newAmount);
            this.lcAmount = newAmount;
            this.availableAmount = newAmount;
        }

        if (newExpiryDate != null && !newExpiryDate.equals(this.expiryDate)) {
            validateDates(this.lastShipmentDate, newExpiryDate, this.presentationDate);
            this.expiryDate = newExpiryDate;
        }

        if (newLastShipmentDate != null && !newLastShipmentDate.equals(this.lastShipmentDate)) {
            validateDates(newLastShipmentDate, this.expiryDate, this.presentationDate);
            this.lastShipmentDate = newLastShipmentDate;
        }

        this.status = LCStatus.AMENDED;
        this.setUpdatedBy(approvedBy);

        registerEvent(new LCAmendmentApproved(
                getId().asString(),
                lcNumber.value(),
                this.lcAmount,
                this.expiryDate,
                approvedBy.toString()
        ));
    }

    /**
     * Reject amendment request
     */
    public void rejectAmendment(String rejectionReason, UUID rejectedBy) {
        if (status != LCStatus.AMENDMENT_REQUESTED) {
            throw LCException.invalidStatus("Only AMENDMENT_REQUESTED LC can be rejected. Current status: " + status);
        }

        if (rejectionReason == null || rejectionReason.isBlank()) {
            throw new IllegalArgumentException("Rejection reason is required");
        }

        this.status = LCStatus.ISSUED; // Return to issued state
        this.setUpdatedBy(rejectedBy);
    }

    /**
     * Receive documents for review
     */
    public void receiveDocuments(UUID receivedBy) {
        if (status != LCStatus.ISSUED) {
            throw LCException.invalidStatus("Documents can only be received for ISSUED LC. Current status: " + status);
        }

        this.status = LCStatus.DOCUMENTS_RECEIVED;
        this.setUpdatedBy(receivedBy);

        registerEvent(new LCDocumentsReceived(
                getId().asString(),
                lcNumber.value()
        ));
    }

    /**
     * Accept documents (no discrepancies or waived)
     */
    public void acceptDocuments(UUID acceptedBy) {
        if (status != LCStatus.DOCUMENTS_RECEIVED && status != LCStatus.DISCREPANCIES_WAIVED) {
            throw LCException.invalidStatus("Invalid status for document acceptance: " + status);
        }

        this.status = LCStatus.DOCUMENTS_ACCEPTED;
        this.setUpdatedBy(acceptedBy);

        registerEvent(new LCDocumentsAccepted(
                getId().asString(),
                lcNumber.value(),
                acceptedBy.toString()
        ));
    }

    /**
     * Reject documents due to discrepancies
     */
    public void rejectDocuments(String discrepancies, UUID rejectedBy) {
        if (status != LCStatus.DOCUMENTS_RECEIVED) {
            throw LCException.invalidStatus("Documents can only be rejected when in DOCUMENTS_RECEIVED status. Current status: " + status);
        }

        if (discrepancies == null || discrepancies.isBlank()) {
            throw new IllegalArgumentException("Discrepancies description is required for document rejection");
        }

        this.status = LCStatus.DOCUMENTS_REJECTED;
        this.setUpdatedBy(rejectedBy);

        registerEvent(new LCDocumentsRejected(
                getId().asString(),
                lcNumber.value(),
                discrepancies,
                rejectedBy.toString()
        ));
    }

    /**
     * Waive discrepancies and accept documents
     */
    public void waiveDiscrepancies(UUID waivedBy) {
        if (status != LCStatus.DOCUMENTS_REJECTED) {
            throw LCException.invalidStatus("Can only waive discrepancies when documents are rejected. Current status: " + status);
        }

        this.status = LCStatus.DISCREPANCIES_WAIVED;
        this.setUpdatedBy(waivedBy);
    }

    /**
     * Authorize payment
     */
    public void authorizePayment(Money paymentAmount, UUID authorizedBy) {
        if (status != LCStatus.DOCUMENTS_ACCEPTED) {
            throw LCException.invalidStatus("Payment can only be authorized after documents are accepted");
        }

        if (paymentAmount.isGreaterThan(availableAmount)) {
            throw LCException.invalidAmount("Payment amount exceeds available LC amount");
        }

        this.status = LCStatus.PAYMENT_AUTHORIZED;
        this.availableAmount = availableAmount.subtract(paymentAmount);
        this.setUpdatedBy(authorizedBy);

        registerEvent(new LCPaymentAuthorized(
                getId().asString(),
                lcNumber.value(),
                paymentAmount,
                beneficiaryId.getValue().toString()
        ));
    }

    /**
     * Mark payment as completed
     */
    public void completePayment(UUID completedBy) {
        if (status != LCStatus.PAYMENT_AUTHORIZED) {
            throw LCException.invalidStatus("Invalid status for payment completion");
        }

        this.status = LCStatus.PAID;
        this.setUpdatedBy(completedBy);

        registerEvent(new LCPaymentCompleted(
                getId().asString(),
                lcNumber.value(),
                availableAmount
        ));
    }

    /**
     * Complete the LC lifecycle
     */
    public void complete(UUID completedBy) {
        if (status != LCStatus.PAID) {
            throw LCException.invalidStatus("LC can only be completed after payment");
        }

        this.status = LCStatus.COMPLETED;
        this.setUpdatedBy(completedBy);
    }

    /**
     * Expire LC (not utilized)
     */
    public void expire() {
        if (status == LCStatus.COMPLETED || status == LCStatus.PAID) {
            throw LCException.invalidStatus("Cannot expire LC in status: " + status);
        }

        this.status = LCStatus.EXPIRED;

        registerEvent(new LCExpired(
                getId().asString(),
                lcNumber.value(),
                expiryDate
        ));
    }

    /**
     * Check if LC is expired
     */
    public boolean isExpired() {
        return expiryDate != null && LocalDate.now().isAfter(expiryDate);
    }

    // Validation methods

    private static void validateParties(CustomerId applicantId, CustomerId beneficiaryId,
                                       String applicantName, String beneficiaryName) {
        if (applicantId == null || beneficiaryId == null) {
            throw LCException.invalidParty("Applicant and Beneficiary must be specified");
        }

        if (applicantId.equals(beneficiaryId)) {
            throw LCException.invalidParty("Applicant and Beneficiary cannot be the same");
        }

        if (applicantName == null || applicantName.isBlank()) {
            throw LCException.invalidParty("Applicant name is required");
        }

        if (beneficiaryName == null || beneficiaryName.isBlank()) {
            throw LCException.invalidParty("Beneficiary name is required");
        }
    }

    private static void validateAmount(Money amount) {
        if (amount == null) {
            throw LCException.invalidAmount("LC amount is required");
        }

        if (amount.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw LCException.invalidAmount("LC amount must be positive");
        }
    }

    private static void validateDates(LocalDate shipmentDate, LocalDate expiryDate, LocalDate presentationDate) {
        LocalDate today = LocalDate.now();

        if (expiryDate != null && expiryDate.isBefore(today)) {
            throw LCException.invalidDates("Expiry date cannot be in the past");
        }

        if (shipmentDate != null && expiryDate != null && shipmentDate.isAfter(expiryDate)) {
            throw LCException.invalidDates("Shipment date cannot be after expiry date");
        }

        if (presentationDate != null && expiryDate != null && presentationDate.isAfter(expiryDate)) {
            throw LCException.invalidDates("Presentation date cannot be after expiry date");
        }
    }

    private void validateForSubmission() {
        if (goodsDescription == null || goodsDescription.isBlank()) {
            throw LCException.validationFailed("Goods description is required");
        }

        if (incoterm == null) {
            throw LCException.validationFailed("Incoterm is required");
        }

        if (lastShipmentDate == null) {
            throw LCException.validationFailed("Last shipment date is required");
        }
    }

    // Builder for aggregate reconstruction
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final LetterOfCredit lc;

        private Builder() {
            this.lc = new LetterOfCredit(LCId.generate());
        }

        public Builder id(LCId id) {
            lc.setId(id);
            return this;
        }

        public Builder lcNumber(LCNumber lcNumber) {
            lc.lcNumber = lcNumber;
            return this;
        }

        public Builder lcType(LCType lcType) {
            lc.lcType = lcType;
            return this;
        }

        public Builder status(LCStatus status) {
            lc.status = status;
            return this;
        }

        public Builder tenantId(UUID tenantId) {
            lc.tenantId = tenantId;
            return this;
        }

        public Builder applicantId(CustomerId applicantId) {
            lc.applicantId = applicantId;
            return this;
        }

        public Builder applicantName(String applicantName) {
            lc.applicantName = applicantName;
            return this;
        }

        public Builder beneficiaryId(CustomerId beneficiaryId) {
            lc.beneficiaryId = beneficiaryId;
            return this;
        }

        public Builder beneficiaryName(String beneficiaryName) {
            lc.beneficiaryName = beneficiaryName;
            return this;
        }

        public Builder lcAmount(Money lcAmount) {
            lc.lcAmount = lcAmount;
            return this;
        }

        public Builder expiryDate(LocalDate expiryDate) {
            lc.expiryDate = expiryDate;
            return this;
        }

        public Builder createdBy(UUID createdBy) {
            lc.setCreatedBy(createdBy);
            return this;
        }

        public Builder version(Long version) {
            lc.setVersion(version);
            return this;
        }

        public LetterOfCredit build() {
            return lc;
        }
    }
}
