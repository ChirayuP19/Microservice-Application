package com.chirayu.order.clients;

import com.chirayu.order.dto.UserResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface UserServiceClient {

    @GetExchange("/api/v1/users/{id}")
    UserResponse getUserDetails(@PathVariable("id") String userId);
}
