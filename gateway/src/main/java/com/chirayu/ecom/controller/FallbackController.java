package com.chirayu.ecom.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/products")
    public ResponseEntity<List<String>> productsFallBack(){
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Collections.
                singletonList("Product Service is unavailable please try after some time..."));
    }

    @RequestMapping("/fallback/users")
    public ResponseEntity<List<String>> usersFallBack(){
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Collections.
                singletonList("User Service is unavailable please try after some time..."));
    }

    @RequestMapping("/fallback/orders")
    public ResponseEntity<List<String>> ordersFallBack(){
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Collections.
                singletonList("Order Service is unavailable please try after some time..."));
    }
}
