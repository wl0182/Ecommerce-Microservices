package com.wassimlagnaoui.ecommerce.Notification_Service.Service;


import com.wassimlagnaoui.common_events.Events.OrderService.OrderCreateEvent;
import com.wassimlagnaoui.common_events.Events.PaymentService.PaymentFailed;
import com.wassimlagnaoui.common_events.Events.PaymentService.PaymentProcessed;
import com.wassimlagnaoui.ecommerce.Notification_Service.DTO.TemplateRequest;
import com.wassimlagnaoui.ecommerce.Notification_Service.DTO.TemplateResponse;
import com.wassimlagnaoui.ecommerce.Notification_Service.Domain.NotificationType;
import com.wassimlagnaoui.ecommerce.Notification_Service.Domain.Template;
import com.wassimlagnaoui.ecommerce.Notification_Service.Exception.InvalidNotificationType;
import com.wassimlagnaoui.ecommerce.Notification_Service.Exception.TemplateNotFound;
import com.wassimlagnaoui.ecommerce.Notification_Service.Repository.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class TemplateService {

    private TemplateRepository templateRepository;
    public TemplateService(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Autowired
    private TemplateEngine templateEngine;

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

    // fetch template from Templates directory under resources
    public String renderUserRegisteredTemplate(String userName) {
        // simple replacement of {{userName}} with actual userName using Thymeleaf
        Context context = new Context();
        context.setVariable("userName", userName); // set the userName variable in the context
        return templateEngine.process("User-Registered", context);
    }



    public String renderOrderCreatedTemplate(OrderCreateEvent order) {

        Context context = new Context();
        context.setVariable("orderId", order.getOrderId()); // set the orderId variable in the context
        context.setVariable("totalAmount", order.getTotalAmount());
        context.setVariable("createdAt", order.getCreatedAt());
        context.setVariable("paymentMethod", order.getPaymentMethod());
        context.setVariable("items", order.getItems());

        return templateEngine.process("Order-Created", context);
    }

    // render payment processed template
    // paymentId, orderId, paymentMethod, status, createdAt
    public String renderPaymentProcessedTemplate(PaymentProcessed paymentProcessed) {
        Context context = new Context();

        context.setVariable("paymentId", paymentProcessed.getPaymentId());
        context.setVariable("orderId", paymentProcessed.getOrderId());
        context.setVariable("paymentMethod", paymentProcessed.getPaymentMethod());
        context.setVariable("status", paymentProcessed.getStatus());
        context.setVariable("createdAt", paymentProcessed.getCreatedAt());


        return templateEngine.process("Payment-Processed", context);
    }



    // render Payment Failed template
    // Variables to be used: paymentId, orderId, amount, failedAt
    public String renderPaymentFailedTemplate(PaymentFailed paymentFailed) {
        Context context = new Context();
        // Add variables to the context as needed
        context.setVariable("paymentId", paymentFailed.getPaymentId());
        context.setVariable("orderId", paymentFailed.getOrderId());
        context.setVariable("amount", paymentFailed.getAmount());
        context.setVariable("failedAt", paymentFailed.getFailedAt());


        return templateEngine.process("Payment-Failed", context);
    }










}
