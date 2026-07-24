package com.tradesphere.ai.workflow.application.service;

import com.tradesphere.ai.workflow.application.port.in.CreateWorkflowCommand;
import com.tradesphere.ai.workflow.application.port.in.CreateWorkflowUseCase;
import com.tradesphere.ai.workflow.application.port.out.PublishWorkflowEventPort;
import com.tradesphere.ai.workflow.application.port.out.WorkflowRepository;
import com.tradesphere.ai.workflow.domain.model.Workflow;
import com.tradesphere.ai.workflow.domain.model.WorkflowId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case implementation for creating workflows
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateWorkflowService implements CreateWorkflowUseCase {

    private final WorkflowRepository workflowRepository;
    private final PublishWorkflowEventPort eventPublisher;

    @Override
    public WorkflowId createWorkflow(CreateWorkflowCommand command) {
        log.info("Creating workflow: type={}, entityType={}, entityId={}",
                command.getType(), command.getEntityType(), command.getEntityId());

        // Create workflow
        Workflow workflow = Workflow.create(
                command.getTenantId(),
                command.getType(),
                command.getEntityType(),
                command.getEntityId(),
                command.getInitiatedBy(),
                command.getInitiatorName(),
                command.getSlaHours()
        );

        // Add tasks
        command.getTasks().forEach(taskDef -> {
            workflow.addTask(
                    taskDef.getTaskName(),
                    taskDef.getDescription(),
                    taskDef.getPriority(),
                    taskDef.getSequenceOrder(),
                    taskDef.getTaskSlaHours()
            );
        });

        // Start workflow
        workflow.start();

        // Assign first level tasks
        command.getTasks().stream()
                .filter(taskDef -> taskDef.getSequenceOrder() == 1)
                .forEach(taskDef -> {
                    if (taskDef.getAssignedTo() != null) {
                        workflow.assignTask(
                                workflow.getTasksForLevel(1).get(0).getId(),
                                taskDef.getAssignedTo(),
                                taskDef.getAssignedToName(),
                                command.getInitiatedBy()
                        );
                    }
                });

        // Save workflow
        Workflow savedWorkflow = workflowRepository.save(workflow);

        // Publish domain events
        savedWorkflow.getDomainEvents().forEach(eventPublisher::publish);
        savedWorkflow.clearDomainEvents();

        log.info("Workflow created: id={}, status={}",
                savedWorkflow.getId(), savedWorkflow.getStatus());

        return savedWorkflow.getId();
    }
}


