package com.learnwords.userservice;

import com.learnwords.userservice.dtos.RegisterRequest;

public class TestDataUtil {
    public static RegisterRequest registerRequest() {
        return RegisterRequest.builder()
                .username("testUser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .build();
    }
}
