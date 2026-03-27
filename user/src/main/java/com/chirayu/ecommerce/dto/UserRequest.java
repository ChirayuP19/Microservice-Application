package com.chirayu.ecommerce.dto;

import com.chirayu.ecommerce.entity.UserRole;
import lombok.Data;

@Data
public class UserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private UserRole role;
    private AddressDTO address;
}
