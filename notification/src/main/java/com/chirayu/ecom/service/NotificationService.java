package com.chirayu.ecom.service;

import com.chirayu.ecom.dto.OrderEvent;
import com.chirayu.ecom.entity.Notification;
import com.chirayu.ecom.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final JavaEmailService emailService;

    public void processOrderNotification(OrderEvent event){
        String status="SENT";
        try {
            emailService.sendOrderConfirmationEmail(event);
        }catch (Exception e){
            log.error("Notification failed for order {}", event.getOrderId());
            status = "FAILED";
        }

        Notification notification= Notification.builder()
                .userId(event.getUserId())
                .email(event.getUserEmail())
                .orderId(event.getOrderId())
                .message("Order #" + event.getOrderId() +
                        " confirmed for $" + event.getTotalAmount())
                .type("EMAIL")
                .status(status)
                .sentAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
        log.info("Notification saved for orderId: {}", event.getOrderId());
    }
}
