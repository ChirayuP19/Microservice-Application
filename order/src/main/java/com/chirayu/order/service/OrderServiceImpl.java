package com.chirayu.order.service;


import com.chirayu.order.clients.ProductServiceClient;
import com.chirayu.order.clients.UserServiceClient;
import com.chirayu.order.dto.OrderEvent;
import com.chirayu.order.dto.OrderItemDTO;
import com.chirayu.order.dto.OrderResponse;
import com.chirayu.order.dto.UserResponse;
import com.chirayu.order.entity.CartItem;
import com.chirayu.order.entity.Order;
import com.chirayu.order.entity.OrderItem;
import com.chirayu.order.entity.OrderStatus;
import com.chirayu.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final UserServiceClient userServiceClient;
    private final ProductServiceClient productServiceClient;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;


    @Override
    public Optional<OrderResponse> createOrder(String userId) {
        List<CartItem> cartItems = cartService.getCart(userId);
        if (cartItems.isEmpty()) {
            return Optional.empty();
        }
        UserResponse userDetails = userServiceClient.getUserDetails(userId);
        if (userDetails == null) {
            return Optional.empty();
        }
        BigDecimal totalPrice = cartItems.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);

        List<OrderItem> orderItems = cartItems
                .stream()
                .map(item -> new OrderItem(
                        null,
                        item.getProductId(),
                        item.getQuantity(),
                        item.getPrice(),
                        order
                )).collect(Collectors.toList());

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(userId);
        savedOrder.getItems().forEach(item->productServiceClient.reduceStock(item.getProductId(),item.getQuantity()));

        kafkaTemplate.send("order.placed",OrderEvent.builder()
                .orderId(savedOrder.getId())
                .userId(userId)
                .userEmail(userDetails.getEmail())
                .userName(userDetails.getFirstName()+ " " +userDetails.getLastName())
                .userPhone(userDetails.getPhone())
                .totalAmount(savedOrder.getTotalAmount())
                .status(savedOrder.getStatus().name())
                .createdAt(savedOrder.getCreatedAt())
                .build());
        return Optional.of(mapToOrderResponse(savedOrder));
    }

    @Override
    public OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getItems().stream()
                        .map(orderItem -> new OrderItemDTO(
                                orderItem.getId(),
                                orderItem.getProductId(),
                                orderItem.getQuantity(),
                                orderItem.getPrice(),
                                orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()))
                        ))
                        .toList(),
                order.getCreatedAt()
        );
    }
}
