package com.chirayu.ecommerce.service;

import com.chirayu.ecommerce.dto.AddressDTO;
import com.chirayu.ecommerce.dto.UserRequest;
import com.chirayu.ecommerce.dto.UserResponse;
import com.chirayu.ecommerce.entity.Address;
import com.chirayu.ecommerce.entity.User;
import com.chirayu.ecommerce.entity.UserRole;
import com.chirayu.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final KeycloakService keycloakService;

    public Page<UserResponse> fetchAllUser(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable)
                .map(this::maptoUserResponse);
    }

    public void createUser(UserRequest userRequest) {

        String keyloackId = null;
        try {
            keyloackId = keycloakService.createUser(
                    userRequest.getFirstName(),
                    userRequest.getLastName(),
                    userRequest.getEmail(),
                    userRequest.getPassword(),
                    userRequest.getRole() != null ? userRequest.getRole() : UserRole.CUSTOMER
            );
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Email already exists")) {
                userRequest.getEmail();
            } else {
                throw e;
            }
        }
        User user = new User();
        updateUserFromRequest(user, userRequest);
        user.setKeycloakId(keyloackId);
        user.setRole(userRequest.getRole()!= null ?userRequest.getRole() : UserRole.CUSTOMER);
        userRepository.save(user);
        System.out.println("User saved in DB");
    }


    public Optional<UserResponse> fetchAllUser(String id) {
        return userRepository.findById(id)
                .map(this::maptoUserResponse);

    }

    public boolean updateAllUser(String id, UserRequest userRequest) {
        return userRepository.findById(id)
                .map(updatedUser -> {
                    updateUserFromRequest(updatedUser, userRequest);
                    userRepository.save(updatedUser);
                    return true;
                })
                .orElse(false);
    }

    private void updateUserFromRequest(User user, UserRequest userRequest) {
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setKeycloakId(user.getKeycloakId());
        user.setPhone(userRequest.getPhone());

        if (userRequest.getAddress() != null) {
            Address address = new Address();
            address.setStreet(userRequest.getAddress().getStreet());
            address.setState(userRequest.getAddress().getState());
            address.setCity(userRequest.getAddress().getCity());
            address.setCountry(userRequest.getAddress().getCountry());
            address.setZipcode(userRequest.getAddress().getZipcode());
            user.setAddress(address);
        }
    }

    private UserResponse maptoUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setKeycloakId(user.getKeycloakId());

        if (user.getAddress() != null) {
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
