package com.wassimlagnaoui.ecommerce.Notification_Service.Service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String body) {
        // Implementation for sending email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("lagnaouiw@gmail.com");
       try {
           mailSender.send(message);
       }
       catch (Exception e){
           System.out.println("Error sending email: " + e.getMessage());
           log.info(e.getMessage());
       }

    }


}
