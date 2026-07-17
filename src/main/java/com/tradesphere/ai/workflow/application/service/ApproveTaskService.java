package com.tradesphere.ai.workflow.application.service;

import com.tradesphere.ai.workflow.application.port.in.ApproveTaskCommand;
import com.tradesphere.ai.workflow.application.port.in.ApproveTaskUseCase;
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
 * Use case implementation for approving workflow tasks
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApproveTaskService implements ApproveTaskUseCase {
    
    private final WorkflowRepository workflowRepository;
    private final PublishWorkflowEventPort eventPublisher;
    
    @Override
    public void approveTask(ApproveTaskCommand command) {
        log.info("Approving task: workflowId={}, taskId={}, userId={}", 
                command.getWorkflowId(), command.getTaskId(), command.getUserId());
        
        // Load workflow
        Workflow workflow = workflowRepository.findById(
                        WorkflowId.of(command.getWorkflowId()),
                        command.getTenantId())
                .orElseThrow(() -> WorkflowException.notFound("Workflow not found: " + command.getWorkflowId()));
        
        // Approve task
        workflow.approveTask(command.getTaskId(), command.getUserId(), command.getComments());
        
        // Save workflow
        Workflow savedWorkflow = workflowRepository.save(workflow);
        
        // Publish domain events
        savedWorkflow.getDomainEvents().forEach(eventPublisher::publish);
        savedWorkflow.clearDomainEvents();
        
        log.info("Task approved: workflowId={}, taskId={}, status={}", 
                workflow.getId(), command.getTaskId(), workflow.getStatus());
    }
}


