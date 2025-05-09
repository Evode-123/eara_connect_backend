package com.earacg.earaConnect.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendCredentials(String email, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Your EARA Connect Account Credentials");
        message.setText("""
            Hello,
            
            Your account has been created in the EARA Connect system.
            
            Username: %s
            Temporary Password: %s
            
            Please note that you will be required to change your password upon first login.
            
            This is a system-generated email. Please do not reply.
            
            Regards,
            EARA Connect Team
            """.formatted(email, password));
        
        mailSender.send(message);
    }
    public void sendEmail(String email, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);
        
        mailSender.send(message);
    }
}