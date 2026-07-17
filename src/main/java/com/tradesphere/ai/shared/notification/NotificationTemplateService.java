package com.tradesphere.ai.shared.notification;

import java.util.Map;

/**
 * Notification template service
 * Handles template rendering for notifications
 */
public interface NotificationTemplateService {

    String renderSubject(String templateId, Map<String, Object> data);

    String renderMessage(String templateId, Map<String, Object> data);

    void registerTemplate(String templateId, String subjectTemplate, String messageTemplate);
}

