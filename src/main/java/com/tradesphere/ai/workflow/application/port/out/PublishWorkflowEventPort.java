package com.tradesphere.ai.workflow.application.port.out;

import com.tradesphere.ai.shared.domain.DomainEvent;

/**
 * Outbound port for publishing workflow events
 */
public interface PublishWorkflowEventPort {

    /**
     * Publish workflow domain event
     *
     * @param event domain event
     */
    void publish(DomainEvent event);
}


