package com.wassimlagnaoui.ecommerce.Notification_Service.Service;

import com.wassimlagnaoui.ecommerce.Notification_Service.DTO.NotificationDTO;
import com.wassimlagnaoui.ecommerce.Notification_Service.DTO.UserDetails;
import com.wassimlagnaoui.ecommerce.Notification_Service.Domain.Notification;
import com.wassimlagnaoui.ecommerce.Notification_Service.Exception.NotificationNotFound;
import com.wassimlagnaoui.ecommerce.Notification_Service.Repository.NotificationRepository;
import com.wassimlagnaoui.ecommerce.Notification_Service.Repository.TemplateRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final TemplateRepository templateRepository;

    @Autowired
    private  RestTemplate restTemplate;


    @Value("${services.user-service.url}")
    private String userServiceUrl;


    public NotificationService(NotificationRepository notificationRepository, TemplateRepository templateRepository) {
        this.notificationRepository = notificationRepository;
        this.templateRepository = templateRepository;
    }


    // get Notification by id
    public NotificationDTO getNotificationById(Long id){
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new NotificationNotFound("Notification not found with id: " + id));

        return NotificationDTO.builder()
                .id(notification.getId())
                .type(notification.getType().name())
                .message(notification.getMessage())
                .status(notification.getStatus().name())
                .createdAt(notification.getCreatedAt())
                .build();

    }

    // get notification by user id
    public List<NotificationDTO> getNotificationsByUserId(Long userId){
        List<Notification> notifications = notificationRepository.findByUserId(userId);

        if (notifications.isEmpty()){
            throw new NotificationNotFound("No notifications found for user with id: " + userId);
        }

        return notifications.stream().map(notification -> NotificationDTO.builder()
                .id(notification.getId())
                .type(notification.getType().name())
                .message(notification.getMessage())
                .status(notification.getStatus().name())
                .createdAt(notification.getCreatedAt())
                .build()).toList();
    }

    // Get user details from User Service
    @CircuitBreaker(name = "user-service", fallbackMethod = "getUserDetailsFallback")
    public UserDetails getUserDetails(Long userId){
        String url = userServiceUrl + "/api/users/" + userId;
        return restTemplate.getForObject(url, UserDetails.class);
    }

    // Fallback method for getUserDetails
    public UserDetails getUserDetailsFallback(Long userId, Throwable throwable) {
        return UserDetails.builder()
                .id(userId)
                .name("Unknown User")
                .email("email not available")
                .build();
    }





}
