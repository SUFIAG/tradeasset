package com.tradesphere.ai.workflow.adapter.out.event;

import com.tradesphere.ai.shared.domain.DomainEvent;
import com.tradesphere.ai.workflow.application.port.out.PublishWorkflowEventPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Stub implementation of WorkflowEventPublisher when Kafka is disabled
 * Just logs events instead of publishing to Kafka
 */
@Component
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "false", matchIfMissing = true)
@Slf4j
public class WorkflowEventPublisherStub implements PublishWorkflowEventPort {

    @Override
    public void publish(DomainEvent event) {
        log.debug("Stub: Would publish workflow event: type={}, aggregateId={} (Kafka disabled)",
                event.getEventType(), event.getAggregateId());
        // No-op - just log the event
    }
}

