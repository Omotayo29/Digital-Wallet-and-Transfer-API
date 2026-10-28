package com.digitalwalletapi.controller;

import com.digitalwalletapi.dto.request.CreateUserRequest;
import com.digitalwalletapi.dto.response.UserResponse;
import com.digitalwalletapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("createUser")
    public ResponseEntity<UserResponse> createUser( @Valid @RequestBody CreateUserRequest createUserRequest){
        UserResponse userCreated = userService.createUser(createUserRequest);
        return ResponseEntity.ok(userCreated);
    }

    @GetMapping("getUser/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID id){
        return ResponseEntity.ok().body(userService.getUserById(id));
    }
}
