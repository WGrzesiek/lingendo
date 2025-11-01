package com.learnwords.apigateway.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session {
    private String id;
    private String userId;
    private String token;
    private String accountType;
    private String userType;
    private Instant expiration;
    private Instant createdAt;
    private Instant updatedAt;
}
