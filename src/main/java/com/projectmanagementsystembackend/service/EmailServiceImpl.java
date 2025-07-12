package com.projectmanagementsystembackend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailServiceImpl implements EmailService{

    Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sender;
    @Autowired
    private TemplateEngine templateEngine;
    @Override
    public void sendEmailWithToken(String userEmail, String link) throws Exception {
        // Prepare the context for the template
        Context context = new Context();
        context.setVariable("invitationLink", link);

        // Process the template into a String
        String htmlContent = templateEngine.process("invitation-template", context);

        // Create and send the email
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setTo(userEmail);
        //future case we can add project name in subject with overview of project
        mimeMessageHelper.setSubject("Invitation to Join Project");
        mimeMessageHelper.setText(htmlContent, true); // true = isHtml

        try {
            javaMailSender.send(mimeMessageHelper.getMimeMessage());
            logger.info("Successfully sent the email");
        } catch (Exception e) {
            logger.info("Exception in mail sending: " + e.getMessage());
            throw new Exception("Error in sending email");
        }
    }
    @Override
    public void sendOtp(String userEmail, String otp) throws MessagingException {
        // Prepare the context for the template
        Context context = new Context();
        context.setVariable("otp", otp);

        // Process the template into a String
        String htmlContent = templateEngine.process("otp-template", context);

        // Create and send the email
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(userEmail);
        helper.setSubject("OTPForLogin -"+ otp);
        helper.setText(htmlContent, true); // true = isHtml

        javaMailSender.send(message);
    }

    @Override
    public void sendWelcomeEmail(String email, String fullName) {
        // Prepare the context for the template
        Context context = new Context();
        context.setVariable("userName", fullName);

        String htmlContent = templateEngine.process("welcome", context);

        // Create and send the email
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("Welcome to Projex");
            mimeMessageHelper.setText(htmlContent, true); // true = isHtml

            javaMailSender.send(mimeMessageHelper.getMimeMessage());
            logger.info("Welcome email sent successfully to {}", email);
        } catch (MailException | MessagingException e) {
            logger.error("Error sending welcome email: {}", e.getMessage());
        }
    }
}
