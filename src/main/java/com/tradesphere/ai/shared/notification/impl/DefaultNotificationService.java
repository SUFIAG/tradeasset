package com.tradesphere.ai.shared.notification.impl;

import com.tradesphere.ai.shared.notification.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Default implementation of NotificationService
 * Supports multiple notification channels with async processing
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultNotificationService implements NotificationService {

    private final EmailNotificationChannel emailChannel;
    private final SMSNotificationChannel smsChannel;
    private final InAppNotificationChannel inAppChannel;
    private final NotificationRepository notificationRepository;
    private final NotificationTemplateService templateService;

    @Override
    @Async
    public CompletableFuture<Boolean> sendAsync(Notification notification) {
        return CompletableFuture.supplyAsync(() -> send(notification));
    }

    @Override
    public boolean send(Notification notification) {
        log.info("Sending notification: type={}, priority={}, channels={}",
            notification.getType(),
            notification.getPriority(),
            notification.getChannels());

        try {
            // Save notification first
            notificationRepository.save(notification);

            // Render template if template ID is provided
            String subject = notification.getSubject();
            String message = notification.getMessage();

            if (notification.getTemplateId() != null) {
                subject = templateService.renderSubject(
                    notification.getTemplateId(),
                    notification.getTemplateData()
                );
                message = templateService.renderMessage(
                    notification.getTemplateId(),
                    notification.getTemplateData()
                );
            }

            // Send through all specified channels
            boolean allSucceeded = true;
            for (NotificationChannel channel : notification.getChannels()) {
                boolean channelSuccess = sendThroughChannel(
                    channel,
                    notification,
                    subject,
                    message
                );
                allSucceeded = allSucceeded && channelSuccess;
            }

            // Update notification status
            if (allSucceeded) {
                notificationRepository.markAsSent(notification.getId(), Instant.now());
            } else {
                notificationRepository.markAsFailed(
                    notification.getId(),
                    "One or more channels failed"
                );
            }

            return allSucceeded;

        } catch (Exception e) {
            log.error("Failed to send notification: {}", notification.getId(), e);
            notificationRepository.markAsFailed(notification.getId(), e.getMessage());
            return false;
        }
    }

    @Override
    @Async
    public CompletableFuture<List<Boolean>> sendBulkAsync(List<Notification> notifications) {
        return CompletableFuture.supplyAsync(() -> {
            List<Boolean> results = new ArrayList<>();
            for (Notification notification : notifications) {
                results.add(send(notification));
            }
            return results;
        });
    }

    @Override
    public void schedule(Notification notification) {
        log.info("Scheduling notification for: {}", notification.getScheduledAt());
        notificationRepository.save(notification);
        // Note: Would integrate with job scheduler (Quartz/Spring Scheduler)
    }

    @Override
    public List<Notification> getNotificationHistory(UUID userId, int limit) {
        return notificationRepository.findByRecipient(userId, limit);
    }

    @Override
    public void markAsRead(UUID notificationId, UUID userId) {
        notificationRepository.markAsRead(notificationId, userId);
    }

    @Override
    public int getUnreadCount(UUID userId) {
        return notificationRepository.countUnread(userId);
    }

    /**
     * Send through specific channel
     */
    private boolean sendThroughChannel(
        NotificationChannel channel,
        Notification notification,
        String subject,
        String message
    ) {
        try {
            return switch (channel) {
                case EMAIL -> emailChannel.send(
                    notification.getRecipientEmails(),
                    subject,
                    message,
                    notification.getPriority()
                );
                case SMS -> smsChannel.send(
                    notification.getRecipientPhones(),
                    message,
                    notification.getPriority()
                );
                case IN_APP -> inAppChannel.send(
                    notification.getRecipientUserIds(),
                    subject,
                    message,
                    notification.getType(),
                    notification.getPriority(),
                    notification.getEntityType(),
                    notification.getEntityId()
                );
                case WEBHOOK, SLACK, TEAMS -> {
                    log.warn("Channel not yet implemented: {}", channel);
                    yield false;
                }
            };
        } catch (Exception e) {
            log.error("Failed to send through channel {}: {}", channel, e.getMessage(), e);
            return false;
        }
    }
}

