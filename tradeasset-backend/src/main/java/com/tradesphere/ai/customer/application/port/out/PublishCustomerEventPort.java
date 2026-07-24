package com.tradesphere.ai.customer.application.port.out;

import com.tradesphere.ai.shared.domain.DomainEvent;

/**
 * Outbound port for publishing customer domain events
 */
public interface PublishCustomerEventPort {
    void publish(DomainEvent event);
}

