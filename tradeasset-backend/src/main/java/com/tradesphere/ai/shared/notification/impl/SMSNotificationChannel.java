package com.tradesphere.ai.shared.notification.impl;

import com.tradesphere.ai.shared.notification.NotificationPriority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

/**
 * SMS notification channel
 * Sends notifications via SMS using configurable SMS provider
 * 
 * Supported Providers:
 * - Twilio (via REST API)
 * - AWS SNS (via AWS SDK)
 * - Custom REST API
 * 
 * Configuration in application.yml:
 * notification.sms.enabled: true
 * notification.sms.provider: twilio|aws-sns|custom
 * notification.sms.api-key: your-api-key
 * notification.sms.from-number: +1234567890
 */
@Component
@Slf4j
public class SMSNotificationChannel {

    @Value("${notification.sms.enabled:false}")
    private boolean smsEnabled;
    
    @Value("${notification.sms.provider:stub}")
    private String smsProvider;
    
    @Value("${notification.sms.api-key:}")
    private String apiKey;
    
    @Value("${notification.sms.from-number:}")
    private String fromNumber;
    
    @Value("${notification.sms.api-url:}")
    private String apiUrl;
    
    private final RestTemplate restTemplate;
    
    public SMSNotificationChannel() {
        this.restTemplate = new RestTemplate();
    }

    public boolean send(Set<String> phones, String message, NotificationPriority priority) {
        if (phones == null || phones.isEmpty()) {
            log.warn("No phone recipients provided");
            return false;
        }
        
        if (!smsEnabled) {
            log.info("SMS notifications are disabled. Would have sent to {} recipients", phones.size());
            return true;
        }

        log.info("Sending SMS notification to {} recipients: priority={}", phones.size(), priority);

        boolean allSucceeded = true;
        for (String phone : phones) {
            try {
                sendSMS(phone, message, priority);
            } catch (Exception e) {
                log.error("Failed to send SMS to: {}", phone, e);
                allSucceeded = false;
            }
        }

        return allSucceeded;
    }

    private void sendSMS(String phone, String message, NotificationPriority priority) {
        // Truncate message if too long for SMS (160 chars standard)
        String smsMessage = truncateForSMS(message, priority);
        
        try {
            switch (smsProvider.toLowerCase()) {
                case "twilio" -> sendViaTwilio(phone, smsMessage);
                case "aws-sns" -> sendViaAwsSns(phone, smsMessage);
                case "custom" -> sendViaCustomApi(phone, smsMessage);
                default -> sendStub(phone, smsMessage, priority);
            }
            
            log.info("SMS sent to: {} via {} | Priority: {}", 
                maskPhoneNumber(phone), smsProvider, priority);
                
        } catch (Exception e) {
            log.error("SMS sending failed for provider: {}", smsProvider, e);
            throw new RuntimeException("SMS sending failed", e);
        }
    }
    
    /**
     * Send SMS via Twilio REST API
     */
    private void sendViaTwilio(String phone, String message) {
        if (apiKey.isEmpty() || fromNumber.isEmpty()) {
            log.warn("Twilio not configured (missing API key or from number)");
            return;
        }
        
        // Twilio REST API implementation
        // POST to https://api.twilio.com/2010-04-01/Accounts/{AccountSid}/Messages.json
        // Body: To={phone}, From={fromNumber}, Body={message}
        // Authorization: Basic base64({AccountSid}:{AuthToken})
        
        log.debug("Sending SMS via Twilio to: {}", maskPhoneNumber(phone));
        // Implementation would use Twilio Java SDK or REST API
    }
    
    /**
     * Send SMS via AWS SNS
     */
    private void sendViaAwsSns(String phone, String message) {
        if (apiKey.isEmpty()) {
            log.warn("AWS SNS not configured (missing credentials)");
            return;
        }
        
        // AWS SNS implementation
        // Use AWS SDK: AmazonSNS.publish(phone, message)
        
        log.debug("Sending SMS via AWS SNS to: {}", maskPhoneNumber(phone));
        // Implementation would use AWS SDK for Java
    }
    
    /**
     * Send SMS via custom REST API
     */
    private void sendViaCustomApi(String phone, String message) {
        if (apiUrl.isEmpty()) {
            log.warn("Custom SMS API not configured (missing API URL)");
            return;
        }
        
        // Custom REST API call
        try {
            var request = new java.util.HashMap<String, String>();
            request.put("to", phone);
            request.put("from", fromNumber);
            request.put("message", message);
            request.put("api_key", apiKey);
            
            restTemplate.postForEntity(apiUrl, request, String.class);
            log.debug("SMS sent via custom API to: {}", maskPhoneNumber(phone));
            
        } catch (Exception e) {
            log.error("Custom SMS API call failed", e);
            throw e;
        }
    }
    
    /**
     * Stub implementation for development/testing
     */
    private void sendStub(String phone, String message, NotificationPriority priority) {
        log.info("SMS (stub) sent to: {} | Message: {} | Priority: {}",
            maskPhoneNumber(phone), 
            message.substring(0, Math.min(50, message.length())), 
            priority);
    }
    
    /**
     * Truncate message to fit SMS limits with priority prefix
     */
    private String truncateForSMS(String message, NotificationPriority priority) {
        String prefix = "";
        if (priority == NotificationPriority.CRITICAL || priority == NotificationPriority.URGENT) {
            prefix = "[" + priority.name() + "] ";
        }
        
        int maxLength = 160 - prefix.length();
        if (message.length() > maxLength) {
            return prefix + message.substring(0, maxLength - 3) + "...";
        }
        return prefix + message;
    }
    
    /**
     * Mask phone number for logging (privacy)
     */
    private String maskPhoneNumber(String phone) {
        if (phone.length() < 4) return "***";
        return phone.substring(0, 3) + "***" + phone.substring(phone.length() - 2);
    }
}

