package com.chirayu.order.service;

import com.chirayu.order.dto.CartItemRequest;
import com.chirayu.order.entity.CartItem;

import java.util.List;

public interface CartService {
    boolean addToCart(String userID, CartItemRequest request);
    boolean deleteItemFromCart(String userId, Long productId);
    List<CartItem> getCart(String userId);
    void clearCart(String userId);
}
