package com.tradesphere.ai.customer.config;

import com.tradesphere.ai.customer.adapter.out.event.CustomerEventPublisher;
import com.tradesphere.ai.customer.adapter.out.persistence.CustomerPersistenceAdapter;
import com.tradesphere.ai.customer.application.service.CustomerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Customer Module
 * Wires up the hexagonal architecture components
 */
@Configuration
public class CustomerConfiguration {

    @Bean
    public CustomerService customerService(
            CustomerPersistenceAdapter persistenceAdapter,
            CustomerEventPublisher eventPublisher) {

        return new CustomerService(
                persistenceAdapter,  // LoadCustomerPort
                persistenceAdapter,  // SaveCustomerPort
                persistenceAdapter,  // CustomerExistsPort
                eventPublisher,      // PublishCustomerEventPort
                persistenceAdapter   // ListCustomersPort
        );
    }
}

