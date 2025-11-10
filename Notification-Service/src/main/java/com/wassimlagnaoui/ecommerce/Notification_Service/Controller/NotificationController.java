package com.wassimlagnaoui.ecommerce.Notification_Service.Controller;


import com.wassimlagnaoui.ecommerce.Notification_Service.DTO.NotificationDTO;
import com.wassimlagnaoui.ecommerce.Notification_Service.DTO.UserDetails;
import com.wassimlagnaoui.ecommerce.Notification_Service.Service.EmailService;
import com.wassimlagnaoui.ecommerce.Notification_Service.Service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final EmailService emailService;

    public NotificationController(NotificationService notificationService, EmailService emailService) {
        this.notificationService = notificationService;
        this.emailService = emailService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getNotificationById(@PathVariable("id") Long id){
        NotificationDTO notificationDTO = notificationService.getNotificationById(id);
        return ResponseEntity.ok(notificationDTO);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUserId(@PathVariable("userId") Long userId){
        List<NotificationDTO> notificationDTOs = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notificationDTOs);
    }

    // Test email sending
    @PostMapping("/sendTestEmail")
    public ResponseEntity<String> sendTestEmail(){
        UserDetails userDetails = new UserDetails();
        userDetails.setEmail("lagnaouiw@gmail.com");
        userDetails.setName("Wassim Lagnaoui");
        emailService.sendEmail(userDetails.getEmail(), "Test Email", "This is a test email.");
        return ResponseEntity.ok("Test email sent to " + userDetails.getEmail());
    }

}
