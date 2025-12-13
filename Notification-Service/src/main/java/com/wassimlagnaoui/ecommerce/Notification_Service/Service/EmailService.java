package com.wassimlagnaoui.ecommerce.Notification_Service.Service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;



    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String body)  {
        // Implementation for sending email

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = null;
        try {
            mimeMessageHelper = new MimeMessageHelper(message,true);  mimeMessageHelper.setFrom("lagnaouiw@gmail.com");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body,true);
        } catch (MessagingException e) {
            log.info(e.getMessage());
        }




       try {
           mailSender.send(message);
       }
       catch (Exception e){
           System.out.println("Error sending email: " + e.getMessage());
           log.info(e.getMessage());
       }

    }


}
