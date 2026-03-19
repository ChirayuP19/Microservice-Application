package com.chirayu.order.controller;

import com.chirayu.order.dto.CartItemRequest;
import com.chirayu.order.entity.CartItem;
import com.chirayu.order.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("")
    public ResponseEntity<String> addToCart(
            @RequestHeader("X-User-Id") String userID,
            @RequestBody CartItemRequest request){
        if (!cartService.addToCart(userID,request)) {
            return ResponseEntity.badRequest().body("Product Out of stock or User not found !");
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeFromCart(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long productId){
        boolean deleted = cartService.deleteItemFromCart(userId,productId);
        return deleted ? ResponseEntity.noContent().build(): ResponseEntity.notFound().build();
    }

    @GetMapping("")
    public ResponseEntity<List<CartItem>> getCart(
            @RequestHeader("X-User-Id") String userId){
        return ResponseEntity.status(HttpStatus.OK).body(cartService.getCart(userId));
    }
}
