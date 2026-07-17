package com.tradesphere.ai.shared.notification.impl;

import com.tradesphere.ai.shared.notification.NotificationTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple template service using placeholder replacement
 * For production, consider using Thymeleaf, FreeMarker, or Mustache
 */
@Service
@Slf4j
public class SimpleTemplateService implements NotificationTemplateService {

    private final Map<String, String> subjectTemplates = new ConcurrentHashMap<>();
    private final Map<String, String> messageTemplates = new ConcurrentHashMap<>();
    private final Pattern placeholderPattern = Pattern.compile("\\{\\{([^}]+)}}");

    public SimpleTemplateService() {
        // Register default templates
        registerDefaultTemplates();
    }

    @Override
    public String renderSubject(String templateId, Map<String, Object> data) {
        String template = subjectTemplates.get(templateId);
        if (template == null) {
            log.warn("Subject template not found: {}", templateId);
            return "Notification";
        }
        return replacePlaceholders(template, data);
    }

    @Override
    public String renderMessage(String templateId, Map<String, Object> data) {
        String template = messageTemplates.get(templateId);
        if (template == null) {
            log.warn("Message template not found: {}", templateId);
            return "";
        }
        return replacePlaceholders(template, data);
    }

    @Override
    public void registerTemplate(String templateId, String subjectTemplate, String messageTemplate) {
        subjectTemplates.put(templateId, subjectTemplate);
        messageTemplates.put(templateId, messageTemplate);
        log.info("Template registered: {}", templateId);
    }

    private String replacePlaceholders(String template, Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            return template;
        }

        Matcher matcher = placeholderPattern.matcher(template);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = data.get(key);
            String replacement = value != null ? value.toString() : "";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private void registerDefaultTemplates() {
        // SLA Breach
        registerTemplate("sla-breach",
            "SLA Breach Alert: {{workflowType}}",
            "⚠️ SLA BREACH\n\n" +
            "Workflow: {{workflowType}}\n" +
            "Reference: {{referenceNumber}}\n" +
            "Due Date: {{dueDate}}\n" +
            "Assigned To: {{assigneeName}}\n\n" +
            "This item is now overdue and requires immediate attention.\n\n" +
            "View Details: {{workflowUrl}}"
        );

        // SLA Warning
        registerTemplate("sla-warning",
            "SLA Warning: {{workflowType}} - {{hoursRemaining}} hours remaining",
            "⏰ SLA WARNING\n\n" +
            "Workflow: {{workflowType}}\n" +
            "Reference: {{referenceNumber}}\n" +
            "Due Date: {{dueDate}}\n" +
            "Time Remaining: {{hoursRemaining}} hours\n\n" +
            "Please complete this task before the deadline.\n\n" +
            "View Details: {{workflowUrl}}"
        );

        // Task Assigned
        registerTemplate("task-assigned",
            "New Task Assigned: {{workflowType}}",
            "📋 NEW TASK\n\n" +
            "You have been assigned a new task:\n\n" +
            "Workflow: {{workflowType}}\n" +
            "Reference: {{referenceNumber}}\n" +
            "Priority: {{priority}}\n" +
            "Due Date: {{dueDate}}\n\n" +
            "Description: {{description}}\n\n" +
            "View Details: {{workflowUrl}}"
        );

        // Task Escalated
        registerTemplate("task-escalated",
            "Escalated Task: {{workflowType}}",
            "🔺 TASK ESCALATED\n\n" +
            "A task has been escalated to you:\n\n" +
            "Workflow: {{workflowType}}\n" +
            "Reference: {{referenceNumber}}\n" +
            "Original Assignee: {{originalAssignee}}\n" +
            "Overdue Since: {{overdueDate}}\n\n" +
            "This requires immediate attention.\n\n" +
            "View Details: {{workflowUrl}}"
        );

        // Approval Required
        registerTemplate("approval-required",
            "Approval Required: {{entityType}}",
            "✅ APPROVAL REQUIRED\n\n" +
            "Entity: {{entityType}}\n" +
            "Reference: {{referenceNumber}}\n" +
            "Submitted By: {{submittedBy}}\n" +
            "Amount: {{amount}}\n\n" +
            "Please review and approve/reject this request.\n\n" +
            "View Details: {{approvalUrl}}"
        );

        // LC Issued
        registerTemplate("lc-issued",
            "Letter of Credit Issued: {{lcNumber}}",
            "📄 LC ISSUED\n\n" +
            "LC Number: {{lcNumber}}\n" +
            "Applicant: {{applicantName}}\n" +
            "Beneficiary: {{beneficiaryName}}\n" +
            "Amount: {{amount}} {{currency}}\n" +
            "Expiry Date: {{expiryDate}}\n\n" +
            "The Letter of Credit has been successfully issued.\n\n" +
            "View Details: {{lcUrl}}"
        );

        // Compliance Alert
        registerTemplate("compliance-alert",
            "🚨 Compliance Alert: {{alertType}}",
            "🚨 COMPLIANCE ALERT\n\n" +
            "Alert Type: {{alertType}}\n" +
            "Entity: {{entityName}}\n" +
            "Severity: {{severity}}\n\n" +
            "Details: {{details}}\n\n" +
            "Immediate review required.\n\n" +
            "View Details: {{alertUrl}}"
        );

        // High Risk Detected
        registerTemplate("high-risk-detected",
            "⚠️ High Risk Detected: {{entityType}}",
            "⚠️ HIGH RISK ALERT\n\n" +
            "Entity: {{entityType}}\n" +
            "Reference: {{referenceNumber}}\n" +
            "Risk Level: {{riskLevel}}\n" +
            "Risk Score: {{riskScore}}\n\n" +
            "Risk Factors:\n{{riskFactors}}\n\n" +
            "Enhanced due diligence required.\n\n" +
            "View Details: {{riskUrl}}"
        );

        log.info("Default notification templates registered");
    }
}

