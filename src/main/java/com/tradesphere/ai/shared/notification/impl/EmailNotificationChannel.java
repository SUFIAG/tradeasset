package com.tradesphere.ai.shared.notification.impl;

import com.tradesphere.ai.shared.notification.NotificationPriority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Set;

/**
 * Email notification channel
 * Sends notifications via email using Spring JavaMailSender
 *
 * Configuration required in application.yml:
 * spring.mail.host, spring.mail.port, spring.mail.username, spring.mail.password
 */
@Component
@Slf4j
public class EmailNotificationChannel {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${notification.email.from:noreply@tradesphere.ai}")
    private String fromEmail;

    @Value("${notification.email.enabled:false}")
    private boolean emailEnabled;

    public boolean send(Set<String> emails, String subject, String message, NotificationPriority priority) {
        if (emails == null || emails.isEmpty()) {
            log.warn("No email recipients provided");
            return false;
        }

        if (!emailEnabled) {
            log.info("Email notifications are disabled. Would have sent to {} recipients: subject={}",
                emails.size(), subject);
            return true;
        }

        if (mailSender == null) {
            log.warn("JavaMailSender not configured. Email not sent to {} recipients", emails.size());
            return false;
        }

        log.info("Sending email notification to {} recipients: subject={}, priority={}",
            emails.size(), subject, priority);

        boolean allSucceeded = true;
        for (String email : emails) {
            try {
                sendEmail(email, subject, message, priority);
            } catch (Exception e) {
                log.error("Failed to send email to: {}", email, e);
                allSucceeded = false;
            }
        }

        return allSucceeded;
    }

    private void sendEmail(String email, String subject, String message, NotificationPriority priority) {
        try {
            // Use MimeMessage for HTML content support
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject(getPriorityPrefix(priority) + subject);
            helper.setText(message, true); // true = HTML content

            // Set priority header for urgent/critical emails
            if (priority == NotificationPriority.URGENT || priority == NotificationPriority.CRITICAL) {
                mimeMessage.setHeader("X-Priority", "1");
                mimeMessage.setHeader("Importance", "high");
            }

            mailSender.send(mimeMessage);
            log.info("Email sent successfully to: {} | Subject: {} | Priority: {}", email, subject, priority);

        } catch (MessagingException | MailException e) {
            log.error("Failed to send email to: {}", email, e);
            throw new RuntimeException("Email sending failed", e);
        }
    }

    /**
     * Add priority prefix to email subject for high-priority notifications
     */
    private String getPriorityPrefix(NotificationPriority priority) {
        return switch (priority) {
            case CRITICAL -> "[CRITICAL] ";
            case URGENT -> "[URGENT] ";
            case HIGH -> "[HIGH] ";
            default -> "";
        };
    }
}

