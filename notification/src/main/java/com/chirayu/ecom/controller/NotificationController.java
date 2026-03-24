package com.chirayu.ecom.controller;

import com.chirayu.ecom.entity.Notification;
import com.chirayu.ecom.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationRepository notificationRepository;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(
            @PathVariable String userId) {
        return ResponseEntity.ok(notificationRepository.findByUserId(userId));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Notification>> getOrderNotifications(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(notificationRepository.findByOrderId(orderId));
    }
}
