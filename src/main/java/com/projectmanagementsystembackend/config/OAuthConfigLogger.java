package com.projectmanagementsystembackend.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OAuthConfigLogger {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @PostConstruct
    public void logValues() {
        // Don't log secrets in real apps! Just for debugging
        System.out.println(">>> Google Client ID: " + clientId);
        System.out.println(">>> Google Client Secret: " + clientSecret);
    }
}
