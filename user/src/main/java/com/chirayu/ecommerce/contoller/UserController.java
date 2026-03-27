package com.chirayu.ecommerce.contoller;

import com.chirayu.ecommerce.dto.UserRequest;
import com.chirayu.ecommerce.dto.UserResponse;
import com.chirayu.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<Page<UserResponse>> getAllUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.fetchAllUser(page, size));
    }

    @PostMapping("")
    public ResponseEntity<String> createUser(@RequestBody UserRequest user){
        userService.createUser(user);
        return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id){

        return userService.fetchAllUser(id)
                .map(ResponseEntity::ok)
                .orElseGet(()->ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<String> updateData(@PathVariable String id, @RequestBody UserRequest userRequest){
        boolean updated = userService.updateAllUser(id, userRequest);
        if(updated)
            return ResponseEntity.status(HttpStatus.OK).body("User Updated Successfully");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
