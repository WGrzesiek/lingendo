package com.learnwords.userservice.service;

import com.learnwords.userservice.dtos.RegisterRequest;
import com.learnwords.userservice.entity.User;
import com.learnwords.userservice.exception.exceptions.EmailAlreadyExistsException;
import com.learnwords.userservice.exception.exceptions.UserNotFoundException;
import com.learnwords.userservice.exception.exceptions.UsernameAlreadyExistsException;
import com.learnwords.userservice.security.AppUserDetails;
import org.springframework.security.core.userdetails.UserDetails;


public interface UserService {

    void registerUser(RegisterRequest registerRequest) throws UsernameAlreadyExistsException, EmailAlreadyExistsException;
    AppUserDetails authenticate(String username, String password);
    AppUserDetails getUserInfo(String userId) throws UserNotFoundException;
}
