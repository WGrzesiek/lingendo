package com.learnwords.userservice.controller;

import com.learnwords.userservice.dtos.ChangePasswordRequest;
import com.learnwords.userservice.dtos.RegisterRequest;
import com.learnwords.userservice.dtos.RegisterResponse;
import com.learnwords.userservice.dtos.UpdateProfileRequest;
import com.learnwords.userservice.dtos.UserProfileResponse;
import com.learnwords.userservice.security.AppUserDetails;
import com.learnwords.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Użytkownicy", description = "Zarządzanie kontem użytkownika")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/register")
    @Operation(summary = "Rejestracja nowego użytkownika")
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

    @GetMapping("/profile")
    @Operation(summary = "Pobierz profil zalogowanego użytkownika")
    public ResponseEntity<UserProfileResponse> getProfile(
            @RequestHeader("X-User-Id") String userId
    ) {
        log.info("Pobieranie profilu dla userId: {}", userId);
        UserProfileResponse profile = userService.getProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    @Operation(summary = "Aktualizuj profil użytkownika", description = "Pozwala zmienić imię, nazwisko i email")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        log.info("Aktualizacja profilu dla userId: {}", userId);
        UserProfileResponse updatedProfile = userService.updateProfile(userId, request);
        return ResponseEntity.ok(updatedProfile);
    }

    @PostMapping("/change-password")
    @Operation(summary = "Zmień hasło", description = "Wymaga podania aktualnego hasła")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        log.info("Zmiana hasła dla userId: {}", userId);
        userService.changePassword(userId, request);
        return ResponseEntity.ok(Map.of("message", "Hasło zostało zmienione pomyślnie"));
    }
}
