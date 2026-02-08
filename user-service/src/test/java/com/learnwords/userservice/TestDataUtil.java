package com.learnwords.userservice;

import com.learnwords.userservice.dtos.RegisterRequest;
import com.learnwords.userservice.enums.AccountType;
import com.learnwords.userservice.enums.UserType;

public class TestDataUtil {
    public static RegisterRequest registerRequest() {
        return RegisterRequest.builder()
                .username("testUser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .userType(UserType.NORMAL)
                .accountType(AccountType.BASIC)
                .build();
    }
}
