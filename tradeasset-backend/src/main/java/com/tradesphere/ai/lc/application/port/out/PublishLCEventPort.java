package com.tradesphere.ai.lc.application.port.out;

import com.tradesphere.ai.shared.domain.DomainEvent;

/**
 * Port for publishing LC domain events
 */
public interface PublishLCEventPort {
    
    void publish(DomainEvent event);
}

