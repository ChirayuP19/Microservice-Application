package com.chirayu.ecommerce.service;

import com.chirayu.ecommerce.dto.UserRequest;
import com.chirayu.ecommerce.dto.UserResponse;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface UserService {
     Page<UserResponse> fetchAllUser(int page,int size);
     void createUser(UserRequest userRequest);
    Optional<UserResponse> fetchAllUser(String id);
    boolean updateAllUser(String id, UserRequest userRequest);
}
