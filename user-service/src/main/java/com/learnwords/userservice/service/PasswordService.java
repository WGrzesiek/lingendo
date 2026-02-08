package com.learnwords.userservice.service;

public interface PasswordService {
    String hashPassword(String password);
    boolean matchPassword(String rawPassword, String encodedPassword);
}
