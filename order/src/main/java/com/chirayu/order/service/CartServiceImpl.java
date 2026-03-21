package com.chirayu.order.service;

import com.chirayu.order.clients.ProductServiceClient;
import com.chirayu.order.clients.UserServiceClient;
import com.chirayu.order.dto.CartItemRequest;
import com.chirayu.order.dto.ProductResponse;
import com.chirayu.order.dto.UserResponse;
import com.chirayu.order.entity.CartItem;
import com.chirayu.order.repository.CartItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;

    @Override
    public boolean addToCart(String userID, CartItemRequest request) {
        ProductResponse productOpt = productServiceClient.getProductDetails(request.getProductId());
        if (productOpt == null)
            return false;
        if (productOpt.getStockQuantity() < request.getQuantity())
            return false;

        UserResponse userOpt = userServiceClient.getUserDetails(userID);
        if (userOpt == null)
            return false;

        CartItem exsistingCartItem = cartItemRepository.findByUserIdAndProductId(userID, request.getProductId());
        if (exsistingCartItem != null) {
            exsistingCartItem.setQuantity(exsistingCartItem.getQuantity() + request.getQuantity());
            exsistingCartItem.setPrice(BigDecimal.valueOf(1000));
            cartItemRepository.save(exsistingCartItem);
        } else {

            CartItem cartItem = new CartItem();
            cartItem.setProductId(request.getProductId());
            cartItem.setUserId(userID);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(BigDecimal.valueOf(1000));
            cartItemRepository.save(cartItem);
        }
        return true;
    }

    @Override
    public boolean deleteItemFromCart(String userId, Long productId) {
        try {
            CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId);

            if (cartItem != null) {
                cartItemRepository.delete(cartItem);
                return true;
            }
        } catch (NumberFormatException e) {
            log.error("Invalid User ID format: {}", userId);
        }
        return false;
    }

    @Override
    public List<CartItem> getCart(String userId) {
        return cartItemRepository.findByUserId(userId);
    }

    @Override
    public void clearCart(String userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}
