package com.tradesphere.ai.shared.notification;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Notification model
 * Represents a notification to be sent to users
 */
@Value
@Builder
public class Notification {

    UUID id;

    UUID tenantId;

    NotificationType type;

    NotificationPriority priority;

    Set<NotificationChannel> channels;

    // Recipients
    Set<UUID> recipientUserIds;
    Set<String> recipientEmails;
    Set<String> recipientPhones;

    // Content
    String subject;
    String message;
    String templateId;
    Map<String, Object> templateData;

    // Context
    String entityType;
    UUID entityId;

    // Metadata
    @Builder.Default
    Instant createdAt = Instant.now();

    Instant scheduledAt;

    boolean sent;

    Instant sentAt;

    String errorMessage;

    public static class NotificationBuilder {
        public NotificationBuilder addChannel(NotificationChannel channel) {
            if (this.channels == null) {
                this.channels = new java.util.HashSet<>();
            }
            this.channels.add(channel);
            return this;
        }

        public NotificationBuilder addRecipient(UUID userId) {
            if (this.recipientUserIds == null) {
                this.recipientUserIds = new java.util.HashSet<>();
            }
            this.recipientUserIds.add(userId);
            return this;
        }

        public NotificationBuilder addEmail(String email) {
            if (this.recipientEmails == null) {
                this.recipientEmails = new java.util.HashSet<>();
            }
            this.recipientEmails.add(email);
            return this;
        }
    }
}

