package com.tradesphere.ai.workflow.application.service;

import com.tradesphere.ai.workflow.application.port.in.RejectTaskCommand;
import com.tradesphere.ai.workflow.application.port.in.RejectTaskUseCase;
import com.tradesphere.ai.workflow.application.port.out.PublishWorkflowEventPort;
import com.tradesphere.ai.workflow.application.port.out.WorkflowRepository;
import com.tradesphere.ai.workflow.domain.model.Workflow;
import com.tradesphere.ai.workflow.domain.model.WorkflowException;
import com.tradesphere.ai.workflow.domain.model.WorkflowId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case implementation for rejecting workflow tasks
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RejectTaskService implements RejectTaskUseCase {
    
    private final WorkflowRepository workflowRepository;
    private final PublishWorkflowEventPort eventPublisher;
    
    @Override
    public void rejectTask(RejectTaskCommand command) {
        log.info("Rejecting task: workflowId={}, taskId={}, userId={}", 
                command.getWorkflowId(), command.getTaskId(), command.getUserId());
        
        // Load workflow
        Workflow workflow = workflowRepository.findById(
                        WorkflowId.of(command.getWorkflowId()),
                        command.getTenantId())
                .orElseThrow(() -> WorkflowException.notFound("Workflow not found: " + command.getWorkflowId()));
        
        // Reject task (fails entire workflow)
        workflow.rejectTask(command.getTaskId(), command.getUserId(), command.getReason());
        
        // Save workflow
        Workflow savedWorkflow = workflowRepository.save(workflow);
        
        // Publish domain events
        savedWorkflow.getDomainEvents().forEach(eventPublisher::publish);
        savedWorkflow.clearDomainEvents();
        
        log.info("Task rejected: workflowId={}, taskId={}, status={}", 
                workflow.getId(), command.getTaskId(), workflow.getStatus());
    }
}


