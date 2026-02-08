package com.learnwords.apigateway.dto;

public record UserMeDto(
        String userId,
        String username,
        String accountType,
        String userType,
        boolean isEnabled
) {
}
