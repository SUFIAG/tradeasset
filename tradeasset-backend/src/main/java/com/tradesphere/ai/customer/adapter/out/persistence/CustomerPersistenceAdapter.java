package com.tradesphere.ai.customer.adapter.out.persistence;

import com.tradesphere.ai.customer.application.port.out.CustomerExistsPort;
import com.tradesphere.ai.customer.application.port.out.ListCustomersPort;
import com.tradesphere.ai.customer.application.port.out.LoadCustomerPort;
import com.tradesphere.ai.customer.application.port.out.SaveCustomerPort;
import com.tradesphere.ai.customer.domain.model.Customer;
import com.tradesphere.ai.customer.domain.model.CustomerId;
import com.tradesphere.ai.customer.domain.model.CustomerStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Persistence Adapter for Customer
 * Implements outbound ports using Spring Data JPA
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerPersistenceAdapter implements
        LoadCustomerPort,
        SaveCustomerPort,
        CustomerExistsPort,
        ListCustomersPort {

    private final CustomerJpaRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public Optional<Customer> loadCustomerById(CustomerId customerId) {
        log.debug("Loading customer by ID: {}", customerId.getValue());
        return customerRepository.findById(customerId.getValue())
                .map(this::toDomainModel);
    }

    @Override
    public Optional<Customer> loadCustomerByNumber(String customerNumber) {
        log.debug("Loading customer by number: {}", customerNumber);
        return customerRepository.findByCustomerNumber(customerNumber)
                .map(this::toDomainModel);
    }

    @Override
    public Customer saveCustomer(Customer customer) {
        log.debug("Saving customer: {}", customer.getCustomerNumber());
        CustomerJpaEntity entity = customerMapper.toJpaEntity(customer);
        CustomerJpaEntity savedEntity = customerRepository.save(entity);
        return toDomainModel(savedEntity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByBusinessRegistrationNumber(String registrationNumber) {
        return customerRepository.existsByBusinessRegistrationNumber(registrationNumber);
    }

    @Override
    public Page<Customer> findAll(Pageable pageable) {
        log.debug("Finding all customers with pagination");
        return customerRepository.findAll(pageable)
                .map(this::toDomainModel);
    }

    @Override
    public Page<Customer> searchByNameOrEmail(String searchTerm, Pageable pageable) {
        log.debug("Searching customers by term: {}", searchTerm);
        return customerRepository.searchByNameOrEmailOrNumber(searchTerm, pageable)
                .map(this::toDomainModel);
    }

    @Override
    public Page<Customer> findByStatus(CustomerStatus status, Pageable pageable) {
        log.debug("Finding customers by status: {}", status);
        return customerRepository.findByStatus(status, pageable)
                .map(this::toDomainModel);
    }

    /**
     * Convert JPA entity to domain model
     * Uses the mapper for proper domain model reconstruction
     */
    private Customer toDomainModel(CustomerJpaEntity entity) {
        return customerMapper.toDomainModel(entity);
    }
}
