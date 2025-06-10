package com.learnwords.userservice.controller;

import com.learnwords.userservice.dtos.LoginRequest;
import com.learnwords.userservice.dtos.LoginResponse;
import com.learnwords.userservice.dtos.RegisterRequest;
import com.learnwords.userservice.dtos.RegisterResponse;
import com.learnwords.userservice.security.AppUserDetails;
import com.learnwords.userservice.service.AuthenticationService;
import com.learnwords.userservice.service.UserService;
import com.learnwords.userservice.service.impl.AuthenticationServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationServiceImpl authenticationService;

    public UserController(UserService userService, AuthenticationServiceImpl authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<RegisterResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest){
        log.info("Registering user with username: {}", registerRequest.getUsername());
        String userId = UUID.randomUUID().toString();
        userService.registerUser(registerRequest, userId);
        log.info("User registered successfully with username: {}",userId);
        RegisterResponse registerResponse = RegisterResponse.builder()
                .username(registerRequest.getUsername())
                .message("User registered successfully")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(registerResponse);
        }

    @PostMapping(path = "/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest){
        AppUserDetails userDetails = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        log.info("User logged in successfully with username: {}", loginRequest.getUsername());
        String token = authenticationService.generateToken(userDetails.getUsername(),userDetails.getId(), userDetails.getAuthorities());
        log.info(userDetails.getAuthorities().toString());
        log.info("Generated token for user: {}", loginRequest.getUsername());
        Long expireIn = authenticationService.getExpireIn();
        LoginResponse loginResponse = new LoginResponse(loginRequest.getUsername(), "User logged in successfully", token, expireIn);
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping(path = "/test")
    public String test() {
        return "test";
    }
}
