package com.sid.app.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.stereotype.Component;

@Component
@Data
public class AppProperties {

    @Value("${stock-market.nifty-50.url}")
    private String nifty50URL;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.email-subject}")
    private String emailSubject;

    @Value("${spring.mail.email-body}")
    private String emailBody;

    @Value("${webclient.config.memory-buffer-size}")
    private int bufferSize;

    @Value("${webclient.config.timeout}")
    private int timeout;

    @Value("${webclient.config.retry.max-retry}")
    private int maxRetry;

    @Value("${webclient.config.retry.delay}")
    private int delay;

}