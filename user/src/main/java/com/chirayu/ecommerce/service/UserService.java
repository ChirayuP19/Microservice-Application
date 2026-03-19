package com.chirayu.ecommerce.service;

import com.chirayu.ecommerce.dto.UserRequest;
import com.chirayu.ecommerce.dto.UserResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {
     List<UserResponse> fetchAllUser();
     void createUser(UserRequest userRequest);
    Optional<UserResponse> fetchAllUser(Long id);
    boolean updateAllUser(Long id, UserRequest userRequest);
}
