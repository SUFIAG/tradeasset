package com.tradesphere.ai.customer.application.port.in;

import com.tradesphere.ai.customer.domain.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Use case for listing and searching customers
 */
public interface ListCustomersUseCase {

    /**
     * List all customers with pagination
     */
    Page<Customer> listCustomers(Pageable pageable);

    /**
     * Search customers by name or email
     */
    Page<Customer> searchCustomers(String searchTerm, Pageable pageable);

    /**
     * List customers by status
     */
    Page<Customer> listCustomersByStatus(String status, Pageable pageable);
}

