package com.tradesphere.ai.shared.notification;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Notification Service Interface
 * Handles sending notifications through various channels
 */
public interface NotificationService {

    /**
     * Send notification asynchronously
     */
    CompletableFuture<Boolean> sendAsync(Notification notification);

    /**
     * Send notification synchronously
     */
    boolean send(Notification notification);

    /**
     * Send bulk notifications
     */
    CompletableFuture<List<Boolean>> sendBulkAsync(List<Notification> notifications);

    /**
     * Schedule notification for later
     */
    void schedule(Notification notification);

    /**
     * Get notification history for user
     */
    List<Notification> getNotificationHistory(UUID userId, int limit);

    /**
     * Mark notification as read
     */
    void markAsRead(UUID notificationId, UUID userId);

    /**
     * Get unread count for user
     */
    int getUnreadCount(UUID userId);
}

