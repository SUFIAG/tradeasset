package com.tradesphere.ai.customer.application.port.out;

import com.tradesphere.ai.customer.domain.model.Customer;
import com.tradesphere.ai.customer.domain.model.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Port for listing and searching customers
 */
public interface ListCustomersPort {

    Page<Customer> findAll(Pageable pageable);

    Page<Customer> searchByNameOrEmail(String searchTerm, Pageable pageable);

    Page<Customer> findByStatus(CustomerStatus status, Pageable pageable);
}

