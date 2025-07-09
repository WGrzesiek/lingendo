package com.learnwords.userservice.controller;

import com.learnwords.userservice.dtos.LoginRequest;
import com.learnwords.userservice.dtos.LoginResponse;
import com.learnwords.userservice.dtos.RegisterRequest;
import com.learnwords.userservice.dtos.RegisterResponse;
import com.learnwords.userservice.security.AppUserDetails;
import com.learnwords.userservice.service.UserService;
import com.learnwords.userservice.service.impl.AuthenticationServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        userService.registerUser(registerRequest);
        log.info("User registered successfully with username: {}", registerRequest.getUsername());
        RegisterResponse registerResponse = RegisterResponse.builder()
                .username(registerRequest.getUsername())
                .message("User registered successfully")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(registerResponse);
        }

    @PostMapping(path = "/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response){
        AppUserDetails userDetails = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        log.info("User logged in successfully with username: {}", loginRequest.getUsername());
        String token = authenticationService.generateToken(userDetails.getUsername(),userDetails.getId(), userDetails.getAuthorities());
        log.info("Generated token for user: {}", loginRequest.getUsername());
        Long expireIn = authenticationService.getExpireIn();
        LoginResponse loginResponse = new LoginResponse(loginRequest.getUsername(), "User logged in successfully", token, expireIn );

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/me")
    public List<String> me(@AuthenticationPrincipal Jwt jwt) {
        return List.of(jwt.getClaimAsString("user_id"), jwt.getClaimAsString("authorities"));
    }

}
