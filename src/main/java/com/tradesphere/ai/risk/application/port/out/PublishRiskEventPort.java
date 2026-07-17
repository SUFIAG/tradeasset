package com.tradesphere.ai.risk.application.port.out;

import com.tradesphere.ai.shared.domain.DomainEvent;

/**
 * Outbound port for publishing risk domain events
 */
public interface PublishRiskEventPort {
    
    /**
     * Publish a risk domain event
     */
    void publish(DomainEvent event);
}

