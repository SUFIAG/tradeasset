package com.tradesphere.ai.lc.application.service;

import com.tradesphere.ai.customer.application.port.out.LoadCustomerPort;
import com.tradesphere.ai.customer.domain.model.Customer;
import com.tradesphere.ai.customer.domain.model.CustomerId;
import com.tradesphere.ai.customer.domain.model.CustomerStatus;
import com.tradesphere.ai.lc.application.port.in.*;
import com.tradesphere.ai.lc.application.port.out.*;
import com.tradesphere.ai.lc.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * LC Service - Implements all LC use cases
 * This is the application layer service that orchestrates LC operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LCService implements
        CreateLCUseCase,
        GetLCUseCase,
        ListLCsUseCase,
        SubmitLCUseCase,
        ApproveLCUseCase,
        RejectLCUseCase,
        IssueLCUseCase,
        ReceiveDocumentsUseCase,
        AcceptDocumentsUseCase,
        RejectDocumentsUseCase,
        WaiveDiscrepanciesUseCase,
        AuthorizePaymentUseCase,
        CompletePaymentUseCase,
        RequestAmendmentUseCase,
        ApproveAmendmentUseCase,
        RejectAmendmentUseCase {

    private final LoadLCPort loadLCPort;
    private final SaveLCPort saveLCPort;
    private final ListLCsPort listLCsPort;
    private final PublishLCEventPort publishEventPort;
    private final LCNumberGeneratorPort numberGeneratorPort;
    private final LoadCustomerPort loadCustomerPort;

    @Override
    public LetterOfCredit createLC(CreateLCCommand command) {
        log.info("Creating LC for applicant: {}", command.getApplicantId());

        // Validate applicant and beneficiary exist and are active
        Customer applicant = validateAndLoadCustomer(command.getApplicantId());
        Customer beneficiary = validateAndLoadCustomer(command.getBeneficiaryId());

        // Business rule: Customer must be ACTIVE to apply for LC
        if (applicant.getStatus() != CustomerStatus.ACTIVE) {
            throw LCException.customerNotActive("Applicant must be ACTIVE to apply for LC. Current status: " + applicant.getStatus());
        }

        // Create LC aggregate
        LetterOfCredit lc = LetterOfCredit.createNew(
                command.getTenantId(),
                CustomerId.of(command.getApplicantId()),
                command.getApplicantName(),
                CustomerId.of(command.getBeneficiaryId()),
                command.getBeneficiaryName(),
                command.getLcAmount(),
                command.getLcType(),
                command.getExpiryDate(),
                command.getCreatedBy()
        );

        // Set additional details
        if (command.getGoodsDescription() != null) {
            lc.updateDetails(
                    command.getGoodsDescription(),
                    command.getIncoterm(),
                    command.getPortOfLoading(),
                    command.getPortOfDischarge(),
                    command.getLastShipmentDate(),
                    command.getCreatedBy()
            );
        }

        // Save LC
        LetterOfCredit savedLC = saveLCPort.saveLC(lc);

        // Publish events
        savedLC.getDomainEvents().forEach(publishEventPort::publish);
        savedLC.clearDomainEvents();

        log.info("LC created successfully: {}", savedLC.getId().asString());
        return savedLC;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LetterOfCredit> getLCById(String lcId) {
        log.debug("Loading LC by ID: {}", lcId);
        return loadLCPort.loadLCById(LCId.from(lcId));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LetterOfCredit> getLCByNumber(String lcNumber) {
        log.debug("Loading LC by number: {}", lcNumber);
        return loadLCPort.loadLCByNumber(lcNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LetterOfCredit> listLCs(Pageable pageable) {
        log.debug("Listing LCs with pagination");
        return listLCsPort.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LetterOfCredit> listLCsByApplicant(String applicantId, Pageable pageable) {
        log.debug("Listing LCs for applicant: {}", applicantId);
        return listLCsPort.findByApplicantId(UUID.fromString(applicantId), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LetterOfCredit> listLCsByStatus(String status, Pageable pageable) {
        log.debug("Listing LCs by status: {}", status);
        LCStatus lcStatus = LCStatus.valueOf(status.toUpperCase());
        return listLCsPort.findByStatus(lcStatus, pageable);
    }

    @Override
    public LetterOfCredit submitForApproval(String lcId, UUID submittedBy) {
        log.info("Submitting LC for approval: {}", lcId);

        LetterOfCredit lc = loadLCPort.loadLCById(LCId.from(lcId))
                .orElseThrow(() -> new IllegalArgumentException("LC not found: " + lcId));

        lc.submitForApproval(submittedBy);

        LetterOfCredit savedLC = saveLCPort.saveLC(lc);

        // Publish events
        savedLC.getDomainEvents().forEach(publishEventPort::publish);
        savedLC.clearDomainEvents();

        log.info("LC submitted for approval: {}", lcId);
        return savedLC;
    }

    @Override
    public LetterOfCredit approveLC(ApproveLCCommand command) {
        log.info("Approving LC: {}", command.getLcId());

        LetterOfCredit lc = loadLCPort.loadLCById(LCId.from(command.getLcId()))
                .orElseThrow(() -> new IllegalArgumentException("LC not found: " + command.getLcId()));

        // Business rule: Check credit limit if needed
        if (command.isCreditLimitApproved()) {
            Customer applicant = validateAndLoadCustomer(lc.getApplicantId().getValue().toString());
            validateCreditLimit(applicant, lc.getLcAmount());
        }

        lc.approve(command.getApprovedBy(), command.isCreditLimitApproved());

        LetterOfCredit savedLC = saveLCPort.saveLC(lc);

        // Publish events
        savedLC.getDomainEvents().forEach(publishEventPort::publish);
        savedLC.clearDomainEvents();

        log.info("LC approved: {}", command.getLcId());
        return savedLC;
    }

    @Override
    public LetterOfCredit rejectLC(String lcId, String reason, UUID rejectedBy) {
        log.info("Rejecting LC: {}", lcId);

        LetterOfCredit lc = loadLCPort.loadLCById(LCId.from(lcId))
                .orElseThrow(() -> new IllegalArgumentException("LC not found: " + lcId));

        lc.reject(rejectedBy, reason);

        LetterOfCredit savedLC = saveLCPort.saveLC(lc);

        // Publish events
        savedLC.getDomainEvents().forEach(publishEventPort::publish);
        savedLC.clearDomainEvents();

        log.info("LC rejected: {}", lcId);
        return savedLC;
    }

    @Override
    public LetterOfCredit issueLC(String lcId, UUID issuedBy) {
        log.info("Issuing LC: {}", lcId);

        LetterOfCredit lc = loadLCPort.loadLCById(LCId.from(lcId))
                .orElseThrow(() -> new IllegalArgumentException("LC not found: " + lcId));

        // Generate LC number
        LCNumber lcNumber = numberGeneratorPort.generateNextLCNumber();

        lc.issue(lcNumber, issuedBy);

        LetterOfCredit savedLC = saveLCPort.saveLC(lc);

        // Publish events
        savedLC.getDomainEvents().forEach(publishEventPort::publish);
        savedLC.clearDomainEvents();

        log.info("LC issued with number: {}", lcNumber.value());
        return savedLC;
    }

    @Override
    public LetterOfCredit receiveDocuments(String lcId, UUID receivedBy) {
        log.info("Receiving documents for LC: {}", lcId);

        LetterOfCredit lc = loadLCPort.loadLCById(LCId.from(lcId))
                .orElseThrow(() -> new IllegalArgumentException("LC not found: " + lcId));

        lc.receiveDocuments(receivedBy);

        LetterOfCredit savedLC = saveLCPort.saveLC(lc);

        // Publish events
        savedLC.getDomainEvents().forEach(publishEventPort::publish);
        savedLC.clearDomainEvents();

        log.info("Documents received for LC: {}", lcId);
        return savedLC;
    }

    @Override
    public LetterOfCredit acceptDocuments(String lcId, UUID acceptedBy) {
        log.info("Accepting documents for LC: {}", lcId);

        LetterOfCredit lc = loadLCPort.loadLCById(LCId.from(lcId))
                .orElseThrow(() -> new IllegalArgumentException("LC not found: " + lcId));

        lc.acceptDocuments(acceptedBy);

        LetterOfCredit savedLC = saveLCPort.saveLC(lc);

        // Publish events
        savedLC.getDomainEvents().forEach(publishEventPort::publish);
        savedLC.clearDomainEvents();

        log.info("Documents accepted for LC: {}", lcId);
        return savedLC;
    }

    @Override
    public LetterOfCredit rejectDocuments(String lcId, String discrepancies, UUID rejectedBy) {
        log.info("Rejecting documents for LC: {}", lcId);

        LetterOfCredit lc = loadLCPort.loadLCById(LCId.from(lcId))
                .orElseThrow(() -> new IllegalArgumentException("LC not found: " + lcId));

        lc.rejectDocuments(discrepancies, rejectedBy);

        LetterOfCredit savedLC = saveLCPort.saveLC(lc);

        // Publish events
        savedLC.getDomainEvents().forEach(publishEventPort::publish);
        savedLC.clearDomainEvents();

        log.info("Documents rejected for LC: {}", lcId);
        return savedLC;
    }

    @Override
    public LetterOfCredit waiveDiscrepancies(String lcId, UUID waivedBy) {
        log.info("Waiving discrepancies for LC: {}", lcId);

        LetterOfCredit lc = loadLCPort.loadLCById(LCId.from(lcId))
                .orElseThrow(() -> new IllegalArgumentException("LC not found: " + lcId));

        lc.waiveDiscrepancies(waivedBy);

        LetterOfCredit savedLC = saveLCPort.saveLC(lc);

        log.info("Discrepancies waived for LC: {}", lcId);
        return savedLC;
    }

    @Override
    public LetterOfCredit authorizePayment(String lcId, com.tradesphere.ai.shared.domain.valueobject.Money paymentAmount, UUID authorizedBy) {
        log.info("Authorizing payment for LC: {}", lcId);

        LetterOfCredit lc = loadLCPort.loadLCById(LCId.from(lcId))
                .orElseThrow(() -> new IllegalArgumentException("LC not found: " + lcId));

        lc.authorizePayment(paymentAmount, authorizedBy);

        LetterOfCredit savedLC = saveLCPort.saveLC(lc);

        // Publish events
        savedLC.getDomainEvents().forEach(publishEventPort::publish);
        savedLC.clearDomainEvents();

        log.info("Payment authorized for LC: {}", lcId);
        return savedLC;
    }

    @Override
    public LetterOfCredit completePayment(String lcId, UUID completedBy) {
        log.info("Completing payment for LC: {}", lcId);

        LetterOfCredit lc = loadLCPort.loadLCById(LCId.from(lcId))
                .orElseThrow(() -> new IllegalArgumentException("LC not found: " + lcId));

        lc.completePayment(completedBy);

        LetterOfCredit savedLC = saveLCPort.saveLC(lc);

        // Publish events
        savedLC.getDomainEvents().forEach(publishEventPort::publish);
        savedLC.clearDomainEvents();

        log.info("Payment completed for LC: {}", lcId);
        return savedLC;
    }

    @Override
    public LetterOfCredit requestAmendment(String lcId, String amendmentDetails, UUID requestedBy) {
        log.info("Requesting amendment for LC: {}", lcId);

        LetterOfCredit lc = loadLCPort.loadLCById(LCId.from(lcId))
                .orElseThrow(() -> new IllegalArgumentException("LC not found: " + lcId));

        lc.requestAmendment(amendmentDetails, requestedBy);

        LetterOfCredit savedLC = saveLCPort.saveLC(lc);

        // Publish events
        savedLC.getDomainEvents().forEach(publishEventPort::publish);
        savedLC.clearDomainEvents();

        log.info("Amendment requested for LC: {}", lcId);
        return savedLC;
    }

    @Override
    public LetterOfCredit approveAmendment(
            String lcId,
            com.tradesphere.ai.shared.domain.valueobject.Money newAmount,
            java.time.LocalDate newExpiryDate,
            java.time.LocalDate newLastShipmentDate,
            UUID approvedBy) {

        log.info("Approving amendment for LC: {}", lcId);

        LetterOfCredit lc = loadLCPort.loadLCById(LCId.from(lcId))
                .orElseThrow(() -> new IllegalArgumentException("LC not found: " + lcId));

        lc.approveAmendment(newAmount, newExpiryDate, newLastShipmentDate, approvedBy);

        LetterOfCredit savedLC = saveLCPort.saveLC(lc);

        // Publish events
        savedLC.getDomainEvents().forEach(publishEventPort::publish);
        savedLC.clearDomainEvents();

        log.info("Amendment approved for LC: {}", lcId);
        return savedLC;
    }

    @Override
    public LetterOfCredit rejectAmendment(String lcId, String rejectionReason, UUID rejectedBy) {
        log.info("Rejecting amendment for LC: {}", lcId);

        LetterOfCredit lc = loadLCPort.loadLCById(LCId.from(lcId))
                .orElseThrow(() -> new IllegalArgumentException("LC not found: " + lcId));

        lc.rejectAmendment(rejectionReason, rejectedBy);

        LetterOfCredit savedLC = saveLCPort.saveLC(lc);

        log.info("Amendment rejected for LC: {}", lcId);
        return savedLC;
    }

    // Helper methods

    private Customer validateAndLoadCustomer(String customerId) {
        return loadCustomerPort.loadCustomerById(CustomerId.of(customerId))
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));
    }

    private void validateCreditLimit(Customer customer, com.tradesphere.ai.shared.domain.valueobject.Money lcAmount) {
        if (customer.getCreditLimit() == null) {
            throw LCException.creditLimitExceeded("Customer does not have a credit limit set");
        }

        if (customer.getAvailableCredit() == null) {
            throw LCException.creditLimitExceeded("Available credit limit not set");
        }

        if (lcAmount.isGreaterThan(customer.getAvailableCredit())) {
            throw LCException.creditLimitExceeded(
                    String.format("LC amount %s exceeds available credit limit %s",
                            lcAmount, customer.getAvailableCredit())
            );
        }
    }
}
