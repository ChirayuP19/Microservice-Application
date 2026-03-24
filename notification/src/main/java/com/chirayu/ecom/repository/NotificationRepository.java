package com.chirayu.ecom.repository;

import com.chirayu.ecom.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification,String> {
    List<Notification>findByUserId(String userId);
    List<Notification>findByOrderId(Long orderId);
}
