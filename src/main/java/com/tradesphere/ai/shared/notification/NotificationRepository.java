package com.tradesphere.ai.shared.notification;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Repository for notification persistence
 */
public interface NotificationRepository {

    void save(Notification notification);

    void markAsSent(UUID notificationId, Instant sentAt);

    void markAsFailed(UUID notificationId, String errorMessage);

    List<Notification> findByRecipient(UUID userId, int limit);

    void markAsRead(UUID notificationId, UUID userId);

    boolean isRead(UUID notificationId, UUID userId);

    int countUnread(UUID userId);

    List<Notification> findScheduledNotifications(Instant now);
}

