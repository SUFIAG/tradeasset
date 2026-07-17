package com.tradesphere.ai.compliance.application.port.out;

import com.tradesphere.ai.shared.domain.DomainEvent;

/**
 * Outbound port for publishing compliance domain events
 */
public interface PublishComplianceEventPort {
    
    /**
     * Publish a compliance domain event
     */
    void publish(DomainEvent event);
}

