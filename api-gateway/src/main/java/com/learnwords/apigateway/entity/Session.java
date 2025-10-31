package com.learnwords.apigateway.entity;

import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.Instant;

@RedisHash("Session")
public class Session implements Serializable {
    private String id;
    private String userId;
    private String token;
    private String accountType;
    private String userType;
    private Instant expiration;
}
