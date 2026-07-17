package com.tradesphere.ai.shared.notification.impl;

import com.tradesphere.ai.shared.notification.Notification;
import com.tradesphere.ai.shared.notification.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of NotificationRepository
 * For production, replace with JPA/database implementation
 */
@Repository
@Slf4j
public class InMemoryNotificationRepository implements NotificationRepository {

    private final Map<UUID, Notification> notifications = new ConcurrentHashMap<>();
    private final Map<UUID, Set<UUID>> userNotifications = new ConcurrentHashMap<>();
    private final Map<UUID, Set<UUID>> readNotifications = new ConcurrentHashMap<>();

    @Override
    public void save(Notification notification) {
        notifications.put(notification.getId(), notification);

        // Index by user IDs
        if (notification.getRecipientUserIds() != null) {
            for (UUID userId : notification.getRecipientUserIds()) {
                userNotifications
                    .computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet())
                    .add(notification.getId());
            }
        }

        log.debug("Notification saved: id={}", notification.getId());
    }

    @Override
    public void markAsSent(UUID notificationId, Instant sentAt) {
        Notification existing = notifications.get(notificationId);
        if (existing != null) {
            Notification updated = Notification.builder()
                .id(existing.getId())
                .tenantId(existing.getTenantId())
                .type(existing.getType())
                .priority(existing.getPriority())
                .channels(existing.getChannels())
                .recipientUserIds(existing.getRecipientUserIds())
                .recipientEmails(existing.getRecipientEmails())
                .recipientPhones(existing.getRecipientPhones())
                .subject(existing.getSubject())
                .message(existing.getMessage())
                .templateId(existing.getTemplateId())
                .templateData(existing.getTemplateData())
                .entityType(existing.getEntityType())
                .entityId(existing.getEntityId())
                .createdAt(existing.getCreatedAt())
                .scheduledAt(existing.getScheduledAt())
                .sent(true)
                .sentAt(sentAt)
                .build();

            notifications.put(notificationId, updated);
            log.debug("Notification marked as sent: id={}", notificationId);
        }
    }

    @Override
    public void markAsFailed(UUID notificationId, String errorMessage) {
        Notification existing = notifications.get(notificationId);
        if (existing != null) {
            Notification updated = Notification.builder()
                .id(existing.getId())
                .tenantId(existing.getTenantId())
                .type(existing.getType())
                .priority(existing.getPriority())
                .channels(existing.getChannels())
                .recipientUserIds(existing.getRecipientUserIds())
                .recipientEmails(existing.getRecipientEmails())
                .recipientPhones(existing.getRecipientPhones())
                .subject(existing.getSubject())
                .message(existing.getMessage())
                .templateId(existing.getTemplateId())
                .templateData(existing.getTemplateData())
                .entityType(existing.getEntityType())
                .entityId(existing.getEntityId())
                .createdAt(existing.getCreatedAt())
                .scheduledAt(existing.getScheduledAt())
                .sent(false)
                .errorMessage(errorMessage)
                .build();

            notifications.put(notificationId, updated);
            log.warn("Notification marked as failed: id={}, error={}", notificationId, errorMessage);
        }
    }

    @Override
    public List<Notification> findByRecipient(UUID userId, int limit) {
        Set<UUID> notificationIds = userNotifications.getOrDefault(userId, Collections.emptySet());

        return notificationIds.stream()
            .map(notifications::get)
            .filter(Objects::nonNull)
            .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(UUID notificationId, UUID userId) {
        readNotifications
            .computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet())
            .add(notificationId);

        log.debug("Notification marked as read: id={}, user={}", notificationId, userId);
    }

    @Override
    public int countUnread(UUID userId) {
        Set<UUID> allNotifications = userNotifications.getOrDefault(userId, Collections.emptySet());
        Set<UUID> readByUser = readNotifications.getOrDefault(userId, Collections.emptySet());

        return (int) allNotifications.stream()
            .filter(id -> !readByUser.contains(id))
            .count();
    }

    @Override
    public List<Notification> findScheduledNotifications(Instant now) {
        return notifications.values().stream()
            .filter(n -> n.getScheduledAt() != null)
            .filter(n -> !n.isSent())
            .filter(n -> n.getScheduledAt().isBefore(now) || n.getScheduledAt().equals(now))
            .collect(Collectors.toList());
    }
}

