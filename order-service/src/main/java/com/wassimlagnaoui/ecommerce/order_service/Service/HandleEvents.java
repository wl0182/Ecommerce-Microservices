package com.wassimlagnaoui.ecommerce.order_service.Service;


import com.wassimlagnaoui.common_events.Events.PaymentService.PaymentProcessed;
import com.wassimlagnaoui.common_events.KafkaGroupIds;
import com.wassimlagnaoui.common_events.KafkaTopics;
import com.wassimlagnaoui.ecommerce.order_service.Entities.Order;
import com.wassimlagnaoui.ecommerce.order_service.Entities.OrderStatus;
import com.wassimlagnaoui.ecommerce.order_service.Exception.OrderNotFound;
import com.wassimlagnaoui.ecommerce.order_service.Repository.OrderItemRepository;
import com.wassimlagnaoui.ecommerce.order_service.Repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HandleEvents {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public HandleEvents(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }


    @KafkaListener(topics = KafkaTopics.PAYMENT_PROCESSED, groupId = KafkaGroupIds.ORDER_SERVICE_GROUP)
    public void handlePaymentProcessed(PaymentProcessed paymentProcessed) {
        Order order = orderRepository.findById(paymentProcessed.getOrderId()).orElseThrow(()-> new OrderNotFound("Order not found with id: " + paymentProcessed.getOrderId()+" for payment processed event"));
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        log.info("Order with id: {} updated to PAID status after payment processed event", order.getId());
    }





}
