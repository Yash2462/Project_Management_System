package com.projectmanagementsystembackend.service;

import jakarta.mail.MessagingException;

public interface EmailService {

    void sendEmailWithToken(String userEmail,String link) throws Exception;
}