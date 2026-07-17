package com.tradesphere.ai.workflow.adapter.out.event;

import com.tradesphere.ai.shared.domain.DomainEvent;
import com.tradesphere.ai.workflow.application.port.out.PublishWorkflowEventPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka adapter for publishing workflow events
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WorkflowEventPublisher implements PublishWorkflowEventPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String WORKFLOW_TOPIC = "workflow-events";

    @Override
    public void publish(DomainEvent event) {
        try {
            log.debug("Publishing workflow event: type={}, aggregateId={}",
                    event.getEventType(), event.getAggregateId());

            kafkaTemplate.send(WORKFLOW_TOPIC, event.getAggregateId().toString(), event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Workflow event published successfully: type={}",
                                    event.getEventType());
                        } else {
                            log.error("Failed to publish workflow event: type={}",
                                    event.getEventType(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing workflow event", e);
            // In production, you might want to store failed events for retry
        }
    }
}


