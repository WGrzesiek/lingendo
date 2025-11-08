package com.learnwords.userservice.controller;

import com.learnwords.userservice.dtos.RegisterRequest;
import com.learnwords.userservice.dtos.RegisterResponse;
import com.learnwords.userservice.security.AppUserDetails;
import com.learnwords.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<RegisterResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest){
        log.info("Registering user with username: {}", registerRequest.getUsername());
        userService.registerUser(registerRequest);
        log.info("User registered successfully with username: {}", registerRequest.getUsername());
        RegisterResponse registerResponse = RegisterResponse.builder()
                .username(registerRequest.getUsername())
                .message("User registered successfully")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(registerResponse);
        }
}
