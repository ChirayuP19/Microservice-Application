package com.chirayu.ecom.service;

import com.chirayu.ecom.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JavaEmailService {

    private final JavaMailSender mailSender;

    public void sendOrderConfirmationEmail(OrderEvent event) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(event.getUserEmail());
            message.setSubject("Order Confirmed - Order #" + event.getOrderId());
            message.setText(
                    "Dear " + event.getUserName() + ",\n\n" +
                            "Your order has been placed successfully!\n\n" +
                            "Order ID    : #" + event.getOrderId() + "\n" +
                            "Total Amount: $" + event.getTotalAmount() + "\n" +
                            "Status      : " + event.getStatus() + "\n" +
                            "Order Date  : " + event.getCreatedAt() + "\n\n" +
                            "Thank you for shopping with us!\n\n" +
                            "Regards,\nE-Com Team"
            );
            mailSender.send(message);
            log.info("Email sent successfully to {}", event.getUserEmail());
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", event.getUserEmail(), e.getMessage());
            throw new RuntimeException("Email sending failed");
        }
    }
}
