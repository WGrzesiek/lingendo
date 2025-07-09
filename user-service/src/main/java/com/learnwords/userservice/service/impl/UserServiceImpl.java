package com.learnwords.userservice.service.impl;

import com.learnwords.userservice.dtos.RegisterRequest;
import com.learnwords.userservice.entity.User;
import com.learnwords.userservice.exception.exceptions.EmailAlreadyExistsException;
import com.learnwords.userservice.exception.exceptions.UsernameAlreadyExistsException;
import com.learnwords.userservice.exception.exceptions.WrongPasswordException;
import com.learnwords.userservice.repository.UserRepository;
import com.learnwords.userservice.security.AppUserDetails;
//import com.learnwords.userservice.service.AppUserDetailService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.learnwords.userservice.service.PasswordService;
import com.learnwords.userservice.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final PasswordService passwordService;
    private final UserRepository userRepository;


    public UserServiceImpl(PasswordService passwordService, UserRepository userRepository) {
        this.passwordService = passwordService;
        this.userRepository = userRepository;

    }

    @Override
    @Transactional
    public void registerUser(RegisterRequest registerRequest) throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        log.info("Registering user with username: {}", registerRequest.getUsername());
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            log.error("Username {} is already taken", registerRequest.getUsername());
            throw new UsernameAlreadyExistsException(registerRequest.getUsername());
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            log.error("Email {} is already registered", registerRequest.getEmail());
            throw new EmailAlreadyExistsException(registerRequest.getEmail());
        }
        log.info("Creating new user with username: {}", registerRequest.getUsername());

        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordService.hashPassword(registerRequest.getPassword()))
                .userType(registerRequest.getUserType())
                .accountType(registerRequest.getAccountType())
                .build();
        userRepository.save(user);
    }

    @Override
    public AppUserDetails authenticate(String username, String password) throws WrongPasswordException, UsernameNotFoundException {
        log.info("Authenticating user with username: {}", username);
        AppUserDetails userDetails = loadUserByUsername(username);
        log.info("User found, checking password for user: {}", username);
        boolean isAuth = passwordService.matchPassword(password, userDetails.getPassword());
        if (!isAuth)
            throw new WrongPasswordException();
        log.info("User {} authenticated successfully", username);
        return userDetails;
    }

    private AppUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        log.info("User found with username: {}", username);
        return new AppUserDetails(user);
    }

    private AppUserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        log.info("Loading user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        log.info("User found with email: {}", email);
        return new AppUserDetails(user);
    }

}

