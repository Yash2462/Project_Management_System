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

@Service
public class EmailServiceImpl implements EmailService{

    Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sender;
    @Override
    public void sendEmailWithToken(String userEmail, String link) throws Exception {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        String subject = "Join project team Invitation";
        String text = "Click the link to join project team"+link;

        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(text,true);
        mimeMessageHelper.setTo(userEmail);

        try {
            javaMailSender.send(mimeMessageHelper.getMimeMessage());
            logger.info("Successfully send the email");
        }catch (Exception e){
            logger.info("Exception in mail sending : "+e.getMessage());
            throw new Exception("Error in sending email");
        }
    }
}
