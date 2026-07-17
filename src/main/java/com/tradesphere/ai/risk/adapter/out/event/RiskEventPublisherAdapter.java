package com.tradesphere.ai.risk.adapter.out.event;

import com.tradesphere.ai.risk.application.port.out.PublishRiskEventPort;
import com.tradesphere.ai.shared.domain.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Adapter for publishing risk domain events
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RiskEventPublisherAdapter implements PublishRiskEventPort {
    
    private final ApplicationEventPublisher eventPublisher;
    
    @Override
    public void publish(DomainEvent event) {
        log.debug("Publishing risk domain event: {}", event.getClass().getSimpleName());
        eventPublisher.publishEvent(event);
    }
}

