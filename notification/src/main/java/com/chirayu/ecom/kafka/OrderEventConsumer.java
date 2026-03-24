package com.chirayu.ecom.kafka;

import com.chirayu.ecom.dto.OrderEvent;
import com.chirayu.ecom.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {
    private final NotificationService notificationService;

    @KafkaListener(topics = "order.placed",groupId = "notification-group")
    public void consumeOrderEvent(OrderEvent event) {
        log.info("Received order event for orderId: {}", event.getOrderId());
        notificationService.processOrderNotification(event);
    }
}
