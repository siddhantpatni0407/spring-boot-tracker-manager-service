package com.sid.app.service;

import com.sid.app.config.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @author Siddhant Patni
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
     * Sends an OTP email to the user
     *
     * @param email Recipient email
     * @param otp   Generated OTP
     */
    public void sendOtpEmail(String email, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(appProperties.getFromEmail());
            message.setTo(email);
            message.setSubject(appProperties.getEmailSubject());
            message.setText("Your OTP for password reset is : " + otp);

            mailSender.send(message);
            log.info("OTP email sent successfully to {}", email);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", email, e.getMessage());
        }
    }

}