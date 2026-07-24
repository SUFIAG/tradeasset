package com.tradesphere.ai.shared.notification.impl;

import com.tradesphere.ai.shared.notification.NotificationPriority;
import com.tradesphere.ai.shared.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-app notification channel
 * Stores notifications in memory/database for in-app display
 * Supports real-time delivery via WebSocket/SSE
 *
 * Features:
 * - Persistent storage for unread notifications
 * - Real-time push to connected users
 * - Read/unread tracking
 * - Notification history
 */
@Component
@Slf4j
public class InAppNotificationChannel {

    // In-memory storage for quick access (would be replaced with database in production)
    private final Map<UUID, java.util.List<InAppNotification>> userNotifications = new ConcurrentHashMap<>();

    @Autowired(required = false)
    private ApplicationEventPublisher eventPublisher;

    public boolean send(
        Set<UUID> userIds,
        String subject,
        String message,
        NotificationType type,
        NotificationPriority priority,
        String entityType,
        UUID entityId
    ) {
        if (userIds == null || userIds.isEmpty()) {
            log.warn("No user recipients provided for in-app notification");
            return false;
        }

        log.info("Creating in-app notifications for {} users: type={}, priority={}",
            userIds.size(), type, priority);

        boolean allSucceeded = true;
        for (UUID userId : userIds) {
            try {
                createInAppNotification(userId, subject, message, type, priority, entityType, entityId);
            } catch (Exception e) {
                log.error("Failed to create in-app notification for user: {}", userId, e);
                allSucceeded = false;
            }
        }

        return allSucceeded;
    }

    private void createInAppNotification(
        UUID userId,
        String subject,
        String message,
        NotificationType type,
        NotificationPriority priority,
        String entityType,
        UUID entityId
    ) {
        // Create notification object
        InAppNotification notification = InAppNotification.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .subject(subject)
            .message(message)
            .type(type)
            .priority(priority)
            .entityType(entityType)
            .entityId(entityId)
            .read(false)
            .createdAt(LocalDateTime.now())
            .build();

        // Store in memory (in production, save to database)
        storeNotification(notification);

        // Publish event for real-time delivery via WebSocket/SSE
        publishNotificationEvent(notification);

        log.info("In-app notification created for user: {} | Type: {} | Subject: {}",
            userId, type, subject);
    }

    /**
     * Store notification (in-memory for now, database in production)
     */
    private void storeNotification(InAppNotification notification) {
        userNotifications.computeIfAbsent(notification.getUserId(),
            k -> new java.util.concurrent.CopyOnWriteArrayList<>())
            .add(notification);

        // In production, save to database:
        // inAppNotificationRepository.save(notification);

        log.debug("Stored in-app notification: id={}, user={}",
            notification.getId(), notification.getUserId());
    }

    /**
     * Publish notification event for real-time delivery
     */
    private void publishNotificationEvent(InAppNotification notification) {
        if (eventPublisher != null) {
            try {
                // Publish event that WebSocket/SSE handlers can listen to
                eventPublisher.publishEvent(new NotificationCreatedEvent(notification));
                log.debug("Published notification event for user: {}", notification.getUserId());
            } catch (Exception e) {
                log.warn("Failed to publish notification event", e);
            }
        }

        // For WebSocket real-time updates (if WebSocket is configured):
        // webSocketService.sendToUser(notification.getUserId(), notification);
    }

    /**
     * Get unread notifications for a user
     */
    public java.util.List<InAppNotification> getUnreadNotifications(UUID userId) {
        return userNotifications.getOrDefault(userId, java.util.List.of())
            .stream()
            .filter(n -> !n.isRead())
            .toList();
    }

    /**
     * Mark notification as read
     */
    public void markAsRead(UUID notificationId, UUID userId) {
        java.util.List<InAppNotification> notifications = userNotifications.get(userId);
        if (notifications != null) {
            notifications.stream()
                .filter(n -> n.getId().equals(notificationId))
                .findFirst()
                .ifPresent(n -> n.setRead(true));

            log.debug("Marked notification as read: id={}, user={}", notificationId, userId);
        }
    }

    /**
     * Get notification count for user
     */
    public int getUnreadCount(UUID userId) {
        return (int) userNotifications.getOrDefault(userId, java.util.List.of())
            .stream()
            .filter(n -> !n.isRead())
            .count();
    }

    /**
     * In-app notification model
     */
    @lombok.Builder
    @lombok.Data
    public static class InAppNotification {
        private UUID id;
        private UUID userId;
        private String subject;
        private String message;
        private NotificationType type;
        private NotificationPriority priority;
        private String entityType;
        private UUID entityId;
        private boolean read;
        private LocalDateTime createdAt;
        private LocalDateTime readAt;
    }

    /**
     * Event published when notification is created (for WebSocket delivery)
     */
    public static class NotificationCreatedEvent {
        private final InAppNotification notification;

        public NotificationCreatedEvent(InAppNotification notification) {
            this.notification = notification;
        }

        public InAppNotification getNotification() {
            return notification;
        }
    }
}

