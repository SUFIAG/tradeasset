package com.tradesphere.ai.shared.application.port.out.notification;

/**
 * Outbound e-mail for password reset and one-time codes. Implemented by the workflow module. When
 * no SMTP server is configured the implementation drops the message and logs only the recipient
 * domain and the template code, never the content.
 */
public interface MailSenderPort {

    /**
     * @param templateCode stable code such as PASSWORD_RESET or LOGIN_OTP, used for logging
     */
    void send(String toAddress, String subject, String body, String templateCode);
}
