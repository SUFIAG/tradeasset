package com.tradesphere.ai.document.application.port.out;

import com.tradesphere.ai.shared.domain.DomainEvent;

/**
 * Port for publishing document domain events
 */
public interface PublishDocumentEventPort {

    void publish(DomainEvent event);
}

