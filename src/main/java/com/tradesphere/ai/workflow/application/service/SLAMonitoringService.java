package com.tradesphere.ai.workflow.application.service;

import com.tradesphere.ai.shared.notification.*;
import com.tradesphere.ai.workflow.application.port.out.WorkflowRepository;
import com.tradesphere.ai.workflow.domain.model.Workflow;
import com.tradesphere.ai.workflow.domain.model.WorkflowTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * SLA monitoring service with notification support
 * Checks for overdue tasks and sends alerts
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SLAMonitoringService {

    private final WorkflowRepository workflowRepository;
    private final NotificationService notificationService;
    private final UserWorkloadService workloadService;

    // Warning thresholds (hours before due)
    private static final int WARNING_24_HOURS = 24;
    private static final int WARNING_4_HOURS = 4;
    private static final int WARNING_1_HOUR = 1;

    /**
     * Check for SLA breaches every 5 minutes
     */
    @Scheduled(fixedDelay = 300000, initialDelay = 60000)
    @Transactional
    public void monitorSLAs() {
        log.debug("Running SLA monitoring check");

        try {
            // Get all active tenants from workflows
            List<UUID> activeTenants = workflowRepository.findActiveTenantIds();

            if (activeTenants.isEmpty()) {
                log.debug("No active tenants found for SLA monitoring");
                return;
            }

            log.info("Monitoring SLAs for {} active tenant(s)", activeTenants.size());

            for (UUID tenantId : activeTenants) {
                try {
                    checkWorkflowSLAs(tenantId);
                } catch (Exception e) {
                    log.error("Error monitoring SLAs for tenant: {}", tenantId, e);
                }
            }
        } catch (Exception e) {
            log.error("Error in SLA monitoring", e);
        }
    }

    private void checkWorkflowSLAs(UUID tenantId) {
        List<Workflow> pendingWorkflows = workflowRepository.findPendingWorkflows(tenantId);

        for (Workflow workflow : pendingWorkflows) {
            checkWorkflowSLA(workflow);
        }
    }

    /**
     * Check individual workflow SLA
     */
    private void checkWorkflowSLA(Workflow workflow) {
        Instant now = Instant.now();

        // Check workflow-level SLA
        if (workflow.getDueDate() != null) {
            long hoursUntilDue = ChronoUnit.HOURS.between(now, workflow.getDueDate());

            // Breach - already overdue
            if (hoursUntilDue < 0 && !workflow.isSlaBreached()) {
                handleSLABreach(workflow, null);
            }
            // Warnings - approaching deadline
            else if (hoursUntilDue == WARNING_1_HOUR) {
                sendSLAWarning(workflow, null, 1);
            } else if (hoursUntilDue == WARNING_4_HOURS) {
                sendSLAWarning(workflow, null, 4);
            } else if (hoursUntilDue == WARNING_24_HOURS) {
                sendSLAWarning(workflow, null, 24);
            }
        }

        // Check task-level SLAs
        for (WorkflowTask task : workflow.getPendingTasks()) {
            checkTaskSLA(workflow, task);
        }
    }

    /**
     * Check individual task SLA
     */
    private void checkTaskSLA(Workflow workflow, WorkflowTask task) {
        if (task.getDueDate() == null) {
            return;
        }

        Instant now = Instant.now();
        long hoursUntilDue = ChronoUnit.HOURS.between(now, task.getDueDate());

        // Breach - already overdue
        if (task.isOverdue()) {
            handleTaskSLABreach(workflow, task);
        }
        // Warnings - approaching deadline
        else if (hoursUntilDue == WARNING_1_HOUR) {
            sendTaskSLAWarning(workflow, task, 1);
        } else if (hoursUntilDue == WARNING_4_HOURS) {
            sendTaskSLAWarning(workflow, task, 4);
        }
    }

    /**
     * Handle workflow SLA breach
     */
    private void handleSLABreach(Workflow workflow, WorkflowTask task) {
        log.warn("Workflow SLA breached: id={}, type={}, dueDate={}",
            workflow.getId(), workflow.getType(), workflow.getDueDate());

        // Save workflow
        workflowRepository.save(workflow);

        // Build notification with channels and recipients
        Notification.NotificationBuilder builder = Notification.builder()
            .id(UUID.randomUUID())
            .tenantId(workflow.getTenantId())
            .type(NotificationType.SLA_BREACH)
            .priority(NotificationPriority.CRITICAL)
            .templateId("sla-breach")
            .templateData(Map.of(
                "workflowType", workflow.getType().name(),
                "referenceNumber", workflow.getEntityId().toString(),
                "dueDate", workflow.getDueDate().toString(),
                "assigneeName", "Workflow Owner",
                "workflowUrl", "/workflows/" + workflow.getId()
            ))
            .entityType("WORKFLOW")
            .entityId(workflow.getId().getValue());

        builder.addChannel(NotificationChannel.EMAIL);
        builder.addChannel(NotificationChannel.SMS);
        builder.addChannel(NotificationChannel.IN_APP);

        if (workflow.getCreatedBy() != null) {
            builder.addRecipient(workflow.getCreatedBy());
        }

        notificationService.sendAsync(builder.build());
    }

    /**
     * Handle task SLA breach and escalate
     */
    private void handleTaskSLABreach(Workflow workflow, WorkflowTask task) {
        log.warn("Task SLA breached: workflowId={}, taskId={}, dueDate={}",
            workflow.getId(), task.getId(), task.getDueDate());

        // Escalate task to supervisor
        UUID supervisorId = findSupervisor(task.getAssignedTo());
        if (supervisorId != null) {
            workflow.escalateTask(task.getId(), supervisorId);
            workflowRepository.save(workflow);

            // Send escalation notification
            sendTaskEscalationNotification(workflow, task, supervisorId);
        }

        // Send breach notification to original assignee
        Notification.NotificationBuilder breachBuilder = Notification.builder()
            .id(UUID.randomUUID())
            .tenantId(workflow.getTenantId())
            .type(NotificationType.TASK_OVERDUE)
            .priority(NotificationPriority.URGENT)
            .templateId("sla-breach")
            .templateData(Map.of(
                "workflowType", task.getTaskName(),
                "referenceNumber", workflow.getEntityId().toString(),
                "dueDate", task.getDueDate().toString(),
                "assigneeName", "You",
                "workflowUrl", "/workflows/" + workflow.getId() + "/tasks/" + task.getId()
            ))
            .entityType("TASK")
            .entityId(task.getId());

        breachBuilder.addChannel(NotificationChannel.EMAIL);
        breachBuilder.addChannel(NotificationChannel.IN_APP);

        if (task.getAssignedTo() != null) {
            breachBuilder.addRecipient(task.getAssignedTo());
        }

        notificationService.sendAsync(breachBuilder.build());
    }

    /**
     * Send SLA warning notification
     */
    private void sendSLAWarning(Workflow workflow, WorkflowTask task, int hoursRemaining) {
        log.info("Sending SLA warning: workflowId={}, hoursRemaining={}",
            workflow.getId(), hoursRemaining);

        NotificationPriority priority = hoursRemaining <= 1 ?
            NotificationPriority.HIGH : NotificationPriority.NORMAL;

        Notification.NotificationBuilder builder = Notification.builder()
            .id(UUID.randomUUID())
            .tenantId(workflow.getTenantId())
            .type(NotificationType.SLA_WARNING)
            .priority(priority)
            .templateId("sla-warning")
            .templateData(Map.of(
                "workflowType", workflow.getType().name(),
                "referenceNumber", workflow.getEntityId().toString(),
                "dueDate", workflow.getDueDate().toString(),
                "hoursRemaining", String.valueOf(hoursRemaining),
                "workflowUrl", "/workflows/" + workflow.getId()
            ))
            .entityType("WORKFLOW")
            .entityId(workflow.getId().getValue());

        builder.addChannel(NotificationChannel.EMAIL);
        builder.addChannel(NotificationChannel.IN_APP);

        if (workflow.getCreatedBy() != null) {
            builder.addRecipient(workflow.getCreatedBy());
        }

        notificationService.sendAsync(builder.build());
    }

    /**
     * Send task SLA warning notification
     */
    private void sendTaskSLAWarning(Workflow workflow, WorkflowTask task, int hoursRemaining) {
        log.info("Sending task SLA warning: taskId={}, hoursRemaining={}",
            task.getId(), hoursRemaining);

        Notification.NotificationBuilder builder = Notification.builder()
            .id(UUID.randomUUID())
            .tenantId(workflow.getTenantId())
            .type(NotificationType.SLA_WARNING)
            .priority(NotificationPriority.HIGH)
            .templateId("sla-warning")
            .templateData(Map.of(
                "workflowType", task.getTaskName(),
                "referenceNumber", workflow.getEntityId().toString(),
                "dueDate", task.getDueDate().toString(),
                "hoursRemaining", String.valueOf(hoursRemaining),
                "workflowUrl", "/workflows/" + workflow.getId() + "/tasks/" + task.getId()
            ))
            .entityType("TASK")
            .entityId(task.getId());

        builder.addChannel(NotificationChannel.EMAIL);
        builder.addChannel(NotificationChannel.IN_APP);

        if (task.getAssignedTo() != null) {
            builder.addRecipient(task.getAssignedTo());
        }

        notificationService.sendAsync(builder.build());
    }

    /**
     * Send task escalation notification
     */
    private void sendTaskEscalationNotification(Workflow workflow, WorkflowTask task, UUID supervisorId) {
        Notification.NotificationBuilder builder = Notification.builder()
            .id(UUID.randomUUID())
            .tenantId(workflow.getTenantId())
            .type(NotificationType.TASK_ESCALATED)
            .priority(NotificationPriority.URGENT)
            .templateId("task-escalated")
            .templateData(Map.of(
                "workflowType", task.getTaskName(),
                "referenceNumber", workflow.getEntityId().toString(),
                "originalAssignee", task.getAssignedTo().toString(),
                "overdueDate", task.getDueDate().toString(),
                "workflowUrl", "/workflows/" + workflow.getId() + "/tasks/" + task.getId()
            ))
            .entityType("TASK")
            .entityId(task.getId());

        builder.addChannel(NotificationChannel.EMAIL);
        builder.addChannel(NotificationChannel.SMS);
        builder.addChannel(NotificationChannel.IN_APP);
        builder.addRecipient(supervisorId);

        notificationService.sendAsync(builder.build());
    }

    /**
     * Find supervisor for escalation
     * Uses organizational hierarchy from UserWorkloadService
     */
    private UUID findSupervisor(UUID userId) {
        if (userId == null) {
            log.warn("Cannot find supervisor for null user");
            return null;
        }

        try {
            // Get supervisor from organizational hierarchy
            UUID supervisorId = workloadService.getSupervisor(userId);

            if (supervisorId != null) {
                log.debug("Found supervisor {} for user {}", supervisorId, userId);
                return supervisorId;
            }

            // Fallback: Find any available manager/supervisor in the system
            List<UUID> availableManagers = workloadService.getAvailableManagers();
            if (!availableManagers.isEmpty()) {
                UUID fallbackSupervisor = availableManagers.get(0);
                log.info("Using fallback supervisor {} for user {}", fallbackSupervisor, userId);
                return fallbackSupervisor;
            }

            log.warn("No supervisor found for user: {}", userId);
            return null;

        } catch (Exception e) {
            log.error("Error finding supervisor for user: {}", userId, e);
            return null;
        }
    }
}



