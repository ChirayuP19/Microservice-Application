package com.chirayu.ecommerce.service;

import com.chirayu.ecommerce.dto.AddressDTO;
import com.chirayu.ecommerce.dto.UserRequest;
import com.chirayu.ecommerce.dto.UserResponse;
import com.chirayu.ecommerce.entity.Address;
import com.chirayu.ecommerce.entity.User;
import com.chirayu.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    public List<UserResponse> fetchAllUser(){
        return userRepository.findAll().stream()
                .map(this::maptoUserResponse)
                .collect(Collectors.toList());
    }

    public void createUser(UserRequest userRequest){
        User user = new User();
        updateUserFromRequest(user,userRequest);
        userRepository.save(user);

    }


    public Optional<UserResponse> fetchAllUser(String id) {
        return userRepository.findById(id)
                .map(this::maptoUserResponse);

    }

    public boolean updateAllUser(String id, UserRequest userRequest) {
        return userRepository.findById(id)
                .map(updatedUser -> {
                    updateUserFromRequest(updatedUser,userRequest);
                    userRepository.save(updatedUser);
                    return true;
                })
                .orElse(false);
    }

    private void updateUserFromRequest(User user, UserRequest userRequest) {
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());

        if(userRequest.getAddress() != null){
            Address address = new Address();
            address.setStreet(userRequest.getAddress().getStreet());
            address.setState(userRequest.getAddress().getState());
            address.setCity(userRequest.getAddress().getCity());
            address.setCountry(userRequest.getAddress().getCountry());
            address.setZipcode(userRequest.getAddress().getZipcode());
            user.setAddress(address);
        }
    }

    private UserResponse maptoUserResponse(User user){
        UserResponse response = new UserResponse();
        response.setId(String.valueOf(user.getId()));
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());

        if(user.getAddress()!=null){
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setStreet(user.getAddress().getStreet());
            addressDTO.setCity(user.getAddress().getCity());
            addressDTO.setState(user.getAddress().getState());
            addressDTO.setCountry(user.getAddress().getCountry());
            addressDTO.setZipcode(user.getAddress().getZipcode());
            response.setAddressDTO(addressDTO);
        }
        return response;
    }
}
