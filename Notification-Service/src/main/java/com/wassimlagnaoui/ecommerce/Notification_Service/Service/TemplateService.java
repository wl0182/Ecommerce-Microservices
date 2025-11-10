package com.wassimlagnaoui.ecommerce.Notification_Service.Service;


import com.wassimlagnaoui.ecommerce.Notification_Service.DTO.TemplateRequest;
import com.wassimlagnaoui.ecommerce.Notification_Service.DTO.TemplateResponse;
import com.wassimlagnaoui.ecommerce.Notification_Service.Domain.NotificationType;
import com.wassimlagnaoui.ecommerce.Notification_Service.Domain.Template;
import com.wassimlagnaoui.ecommerce.Notification_Service.Exception.InvalidNotificationType;
import com.wassimlagnaoui.ecommerce.Notification_Service.Exception.TemplateNotFound;
import com.wassimlagnaoui.ecommerce.Notification_Service.Repository.TemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TemplateService {

    private TemplateRepository templateRepository;
    public TemplateService(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    // get Template by id
    @Transactional(readOnly = true)
    public TemplateResponse getTemplateById(Long id){
        Template template = templateRepository.findById(id).orElseThrow(() -> new RuntimeException("Template not found with id: " + id));

        return TemplateResponse.builder()
                .id(template.getId())
                .type(template.getType().name())
                .subject(template.getSubject())
                .body(template.getBody())
                .build();
    }


    // create template
    @Transactional
    public TemplateResponse createTemplate(TemplateRequest templateRequest){
        Template template = new Template();

        try {
            NotificationType.valueOf(templateRequest.getType());
        } catch (IllegalArgumentException e) {
            throw new InvalidNotificationType("Invalid notification type: " + templateRequest.getType());
        }

        template.setSubject(templateRequest.getSubject());
        template.setBody(templateRequest.getBody());
        Template savedTemplate = templateRepository.save(template);
        return TemplateResponse.builder()
                .id(savedTemplate.getId())
                .type(savedTemplate.getType().name())
                .subject(savedTemplate.getSubject())
                .body(savedTemplate.getBody())
                .build();

    }


    // update template
    @Transactional
    public TemplateResponse updateTemplate(Long id, TemplateRequest templateRequest) {

        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new TemplateNotFound("Template not found with id: " + id));


        NotificationType notificationType;
        try {
            notificationType = NotificationType.valueOf(templateRequest.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidNotificationType("Invalid notification type: " + templateRequest.getType());
        }


        template.setType(notificationType);
        template.setSubject(templateRequest.getSubject());
        template.setBody(templateRequest.getBody());


        Template updatedTemplate = templateRepository.save(template);
        return TemplateResponse.builder()
                .id(updatedTemplate.getId())
                .type(updatedTemplate.getType().name())
                .subject(updatedTemplate.getSubject())
                .body(updatedTemplate.getBody())
                .build();
    }


}
