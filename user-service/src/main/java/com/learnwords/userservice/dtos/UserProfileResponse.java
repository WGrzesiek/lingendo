package com.learnwords.userservice.dtos;

import com.learnwords.userservice.enums.AccountType;
import com.learnwords.userservice.enums.UserType;

import java.time.Instant;

/**
 * DTO z danymi profilu użytkownika.
 */
public record UserProfileResponse(
        String id,
        String username,
        String email,
        String firstName,
        String lastName,
        UserType userType,
        AccountType accountType,
        Instant createdAt,
        Instant lastLogin,
        int streak
) {}
