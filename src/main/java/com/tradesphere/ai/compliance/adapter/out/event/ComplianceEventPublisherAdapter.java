package com.tradesphere.ai.compliance.adapter.out.event;

import com.tradesphere.ai.compliance.application.port.out.PublishComplianceEventPort;
import com.tradesphere.ai.shared.domain.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Adapter for publishing compliance domain events
 * Uses Spring's event publishing mechanism
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ComplianceEventPublisherAdapter implements PublishComplianceEventPort {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(DomainEvent event) {
        log.debug("Publishing compliance domain event: {}", event.getClass().getSimpleName());
        eventPublisher.publishEvent(event);
    }
}

