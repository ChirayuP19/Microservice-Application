package com.chirayu.order.service;

import com.chirayu.order.dto.OrderResponse;
import com.chirayu.order.entity.Order;

import java.util.Optional;

public interface OrderService {
    Optional<OrderResponse> createOrder(String userId);
    OrderResponse mapToOrderResponse(Order order);
}
