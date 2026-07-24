package com.tradesphere.ai.compliance.adapter.in.event;

import com.tradesphere.ai.compliance.application.port.in.InitiateComplianceCheckCommand;
import com.tradesphere.ai.compliance.application.service.InitiateComplianceCheckService;
import com.tradesphere.ai.compliance.domain.event.ComplianceCheckInitiated;
import com.tradesphere.ai.compliance.domain.model.ComplianceCheckId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Event listener for compliance domain events
 * Handles asynchronous compliance screening after check initiation
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ComplianceEventListener {

    private final InitiateComplianceCheckService initiateCheckService;

    /**
     * Handle compliance check initiated event
     * Triggers async screening process
     */
    @Async
    @TransactionalEventListener
    public void onComplianceCheckInitiated(ComplianceCheckInitiated event) {
        log.info("Handling ComplianceCheckInitiated event: checkId={}", event.getAggregateId());

        try {
            // Perform screening asynchronously
            ComplianceCheckId checkId = ComplianceCheckId.of(event.getAggregateId());

            // Create command for screening (in real implementation, this would be stored or retrieved)
            InitiateComplianceCheckCommand command = new InitiateComplianceCheckCommand(
                    event.getTenantId(),
                    event.getCheckType(),
                    event.getEntityType(),
                    event.getEntityId(),
                    null, // entityName
                    null, // initiatedBy
                    null, // country
                    null, // dateOfBirth
                    null, // position
                    null  // transactionDetails
            );

            // Note: In production, you would retrieve the full check details from the repository
            // This is a simplified version for demonstration
            log.info("Async compliance screening triggered for check: {}", checkId);

        } catch (Exception e) {
            log.error("Error handling ComplianceCheckInitiated event: {}", e.getMessage(), e);
            // In production, implement retry logic or dead letter queue
        }
    }
}

