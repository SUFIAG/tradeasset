package com.tradesphere.ai.customer.application.service;

import com.tradesphere.ai.customer.application.port.in.*;
import com.tradesphere.ai.customer.application.port.out.*;
import com.tradesphere.ai.customer.domain.model.Customer;
import com.tradesphere.ai.customer.domain.model.CustomerId;
import com.tradesphere.ai.customer.domain.model.CustomerException;
import com.tradesphere.ai.customer.domain.model.CustomerStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Customer Service - Implements all customer use cases
 * This is the application layer service that orchestrates customer operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerService implements
        CreateCustomerUseCase,
        GetCustomerUseCase,
        UpdateCustomerUseCase,
        ApproveCustomerUseCase,
        RejectCustomerUseCase,
        SuspendCustomerUseCase,
        CompleteKYCUseCase,
        SetCreditLimitUseCase,
        UpdateRiskRatingUseCase,
        SubmitForApprovalUseCase,
        ListCustomersUseCase {

    private final LoadCustomerPort loadCustomerPort;
    private final SaveCustomerPort saveCustomerPort;
    private final CustomerExistsPort customerExistsPort;
    private final PublishCustomerEventPort publishEventPort;
    private final ListCustomersPort listCustomersPort;

    @Override
    public Customer createCustomer(CreateCustomerCommand command) {
        log.info("Creating customer: {}", command.getName());

        // Business rule: Email must be unique
        if (customerExistsPort.existsByEmail(command.getEmail())) {
            throw new CustomerException("Customer with email " + command.getEmail() + " already exists");
        }

        // Business rule: Business registration number must be unique (if provided)
        if (command.getBusinessRegistrationNumber() != null &&
                customerExistsPort.existsByBusinessRegistrationNumber(command.getBusinessRegistrationNumber())) {
            throw new CustomerException("Customer with registration number " +
                    command.getBusinessRegistrationNumber() + " already exists");
        }

        // Create customer aggregate
        Customer customer = Customer.create(
                command.getTenantId(),
                command.getName(),
                command.getLegalName(),
                command.getType(),
                command.getEmail(),
                command.getCreatedBy()
        );

        // Set optional fields
        if (command.getAddress() != null) {
            customer.setAddress(command.getAddress());
        }

        // Save customer
        Customer savedCustomer = saveCustomerPort.saveCustomer(customer);

        // Publish domain events
        savedCustomer.getDomainEvents().forEach(publishEventPort::publish);
        savedCustomer.clearDomainEvents();

        log.info("Customer created successfully: {}", savedCustomer.getCustomerNumber());
        return savedCustomer;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerById(String customerId) {
        log.debug("Retrieving customer by ID: {}", customerId);
        return loadCustomerPort.loadCustomerById(CustomerId.of(customerId));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerByNumber(String customerNumber) {
        log.debug("Retrieving customer by number: {}", customerNumber);
        return loadCustomerPort.loadCustomerByNumber(customerNumber);
    }

    @Override
    public Customer updateCustomer(UpdateCustomerCommand command) {
        log.info("Updating customer: {}", command.getCustomerId());

        Customer customer = loadCustomerPort.loadCustomerById(CustomerId.of(command.getCustomerId()))
                .orElseThrow(() -> new CustomerException("Customer not found: " + command.getCustomerId()));

        customer.updateBasicInfo(
                command.getName(),
                command.getLegalName(),
                command.getEmail(),
                command.getPhone(),
                command.getUpdatedBy()
        );

        if (command.getAddress() != null) {
            customer.setAddress(command.getAddress());
        }

        Customer savedCustomer = saveCustomerPort.saveCustomer(customer);
        savedCustomer.getDomainEvents().forEach(publishEventPort::publish);
        savedCustomer.clearDomainEvents();

        log.info("Customer updated successfully: {}", savedCustomer.getCustomerNumber());
        return savedCustomer;
    }

    @Override
    public Customer submitForApproval(String customerId, UUID submittedBy) {
        log.info("Submitting customer for approval: {}", customerId);

        Customer customer = loadCustomerPort.loadCustomerById(CustomerId.of(customerId))
                .orElseThrow(() -> new CustomerException("Customer not found: " + customerId));

        customer.submitForApproval(submittedBy);

        Customer savedCustomer = saveCustomerPort.saveCustomer(customer);
        savedCustomer.getDomainEvents().forEach(publishEventPort::publish);
        savedCustomer.clearDomainEvents();

        log.info("Customer submitted for approval: {}", savedCustomer.getCustomerNumber());
        return savedCustomer;
    }

    @Override
    public Customer approveCustomer(ApproveCustomerCommand command) {
        log.info("Approving customer: {}", command.getCustomerId());

        Customer customer = loadCustomerPort.loadCustomerById(CustomerId.of(command.getCustomerId()))
                .orElseThrow(() -> new CustomerException("Customer not found: " + command.getCustomerId()));

        customer.approve(command.getApprovedBy());

        Customer savedCustomer = saveCustomerPort.saveCustomer(customer);
        savedCustomer.getDomainEvents().forEach(publishEventPort::publish);
        savedCustomer.clearDomainEvents();

        log.info("Customer approved: {}", savedCustomer.getCustomerNumber());
        return savedCustomer;
    }

    @Override
    public Customer activateCustomer(String customerId, UUID activatedBy) {
        log.info("Activating customer: {}", customerId);

        Customer customer = loadCustomerPort.loadCustomerById(CustomerId.of(customerId))
                .orElseThrow(() -> new CustomerException("Customer not found: " + customerId));

        customer.activate(activatedBy);

        Customer savedCustomer = saveCustomerPort.saveCustomer(customer);
        savedCustomer.getDomainEvents().forEach(publishEventPort::publish);
        savedCustomer.clearDomainEvents();

        log.info("Customer activated: {}", savedCustomer.getCustomerNumber());
        return savedCustomer;
    }

    @Override
    public Customer rejectCustomer(String customerId, String reason, UUID rejectedBy) {
        log.info("Rejecting customer: {}", customerId);

        Customer customer = loadCustomerPort.loadCustomerById(CustomerId.of(customerId))
                .orElseThrow(() -> new CustomerException("Customer not found: " + customerId));

        customer.reject(reason, rejectedBy);

        Customer savedCustomer = saveCustomerPort.saveCustomer(customer);
        savedCustomer.getDomainEvents().forEach(publishEventPort::publish);
        savedCustomer.clearDomainEvents();

        log.info("Customer rejected: {}", savedCustomer.getCustomerNumber());
        return savedCustomer;
    }

    @Override
    public Customer suspendCustomer(String customerId, String reason, UUID suspendedBy) {
        log.info("Suspending customer: {}", customerId);

        Customer customer = loadCustomerPort.loadCustomerById(CustomerId.of(customerId))
                .orElseThrow(() -> new CustomerException("Customer not found: " + customerId));

        customer.suspend(reason, suspendedBy);

        Customer savedCustomer = saveCustomerPort.saveCustomer(customer);
        savedCustomer.getDomainEvents().forEach(publishEventPort::publish);
        savedCustomer.clearDomainEvents();

        log.info("Customer suspended: {}", savedCustomer.getCustomerNumber());
        return savedCustomer;
    }

    @Override
    public Customer reactivateCustomer(String customerId, UUID reactivatedBy) {
        log.info("Reactivating customer: {}", customerId);

        Customer customer = loadCustomerPort.loadCustomerById(CustomerId.of(customerId))
                .orElseThrow(() -> new CustomerException("Customer not found: " + customerId));

        customer.reactivate(reactivatedBy);

        Customer savedCustomer = saveCustomerPort.saveCustomer(customer);
        savedCustomer.getDomainEvents().forEach(publishEventPort::publish);
        savedCustomer.clearDomainEvents();

        log.info("Customer reactivated: {}", savedCustomer.getCustomerNumber());
        return savedCustomer;
    }

    @Override
    public Customer completeKYC(CompleteKYCCommand command) {
        log.info("Completing KYC for customer: {}", command.getCustomerId());

        Customer customer = loadCustomerPort.loadCustomerById(CustomerId.of(command.getCustomerId()))
                .orElseThrow(() -> new CustomerException("Customer not found: " + command.getCustomerId()));

        customer.completeKYC(command.getExpiryDate(), command.getCompletedBy());

        Customer savedCustomer = saveCustomerPort.saveCustomer(customer);
        savedCustomer.getDomainEvents().forEach(publishEventPort::publish);
        savedCustomer.clearDomainEvents();

        log.info("KYC completed for customer: {}", savedCustomer.getCustomerNumber());
        return savedCustomer;
    }

    @Override
    public Customer setCreditLimit(SetCreditLimitCommand command) {
        log.info("Setting credit limit for customer: {}", command.getCustomerId());

        Customer customer = loadCustomerPort.loadCustomerById(CustomerId.of(command.getCustomerId()))
                .orElseThrow(() -> new CustomerException("Customer not found: " + command.getCustomerId()));

        customer.setCreditLimit(command.getCreditLimit(), command.getApprovedBy());

        Customer savedCustomer = saveCustomerPort.saveCustomer(customer);
        savedCustomer.getDomainEvents().forEach(publishEventPort::publish);
        savedCustomer.clearDomainEvents();

        log.info("Credit limit set for customer: {}", savedCustomer.getCustomerNumber());
        return savedCustomer;
    }

    @Override
    public Customer updateRiskRating(UpdateRiskRatingCommand command) {
        log.info("Updating risk rating for customer: {}", command.getCustomerId());

        Customer customer = loadCustomerPort.loadCustomerById(CustomerId.of(command.getCustomerId()))
                .orElseThrow(() -> new CustomerException("Customer not found: " + command.getCustomerId()));

        customer.updateRiskRating(
                command.getRiskRating(),
                command.getRiskScore(),
                command.getReason(),
                command.getUpdatedBy()
        );

        Customer savedCustomer = saveCustomerPort.saveCustomer(customer);
        savedCustomer.getDomainEvents().forEach(publishEventPort::publish);
        savedCustomer.clearDomainEvents();

        log.info("Risk rating updated for customer: {}", savedCustomer.getCustomerNumber());
        return savedCustomer;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Customer> listCustomers(Pageable pageable) {
        log.debug("Listing customers with pagination: {}", pageable);
        return listCustomersPort.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Customer> searchCustomers(String searchTerm, Pageable pageable) {
        log.debug("Searching customers by term: {}", searchTerm);
        return listCustomersPort.searchByNameOrEmail(searchTerm, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Customer> listCustomersByStatus(String status, Pageable pageable) {
        log.debug("Listing customers by status: {}", status);
        CustomerStatus customerStatus = CustomerStatus.valueOf(status.toUpperCase());
        return listCustomersPort.findByStatus(customerStatus, pageable);
    }
}
