package com.sid.app.service;

import com.sid.app.config.AppProperties;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Email Service for sending OTP emails.
 * <p>
 * Author: Siddhant Patni
 */
@Service
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final AppProperties appProperties;

    public EmailService(JavaMailSender mailSender, AppProperties appProperties) {
        this.mailSender = mailSender;
        this.appProperties = appProperties;
    }

    /**
     * Sends an OTP email with a dynamic HTML template.
     *
     * @param email    Recipient email
     * @param userName User's name
     * @param otp      Generated OTP
     */
    public void sendOtpEmail(String email, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Load HTML template from application.yml
            String emailBody = appProperties.getEmailBody()
                    .replace("{EMAIL}", email)
                    .replace("{OTP_CODE}", otp);

            helper.setFrom(appProperties.getFromEmail());
            helper.setTo(email);
            helper.setSubject(appProperties.getEmailSubject());
            helper.setText(emailBody, true); // Enable HTML

            mailSender.send(message);
            log.info("✅ OTP email sent successfully to {}", email);
        } catch (Exception e) {
            log.error("❌ Failed to send OTP email to {}: {}", email, e.getMessage());
        }
    }

}