package com.learnwords.userservice.service.impl;

import com.learnwords.common.events.UserLoginEvent;
import com.learnwords.userservice.dtos.RegisterRequest;
import com.learnwords.userservice.entity.User;
import com.learnwords.userservice.events.UserLoginEventProducer;
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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;


@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final PasswordService passwordService;
    private final UserRepository userRepository;
    private final UserLoginEventProducer userLoginEventProducer;


    public UserServiceImpl(PasswordService passwordService, UserRepository userRepository, UserLoginEventProducer userLoginEventProducer) {
        this.passwordService = passwordService;
        this.userRepository = userRepository;
        this.userLoginEventProducer = userLoginEventProducer;

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
        User user = loadUserByUsername(username);
        AppUserDetails userDetails = new AppUserDetails(user);
        log.info("User found, checking password for user: {}", username);
        boolean isAuth = passwordService.matchPassword(password, userDetails.getPassword());
        if (!isAuth)
            throw new WrongPasswordException();
        log.info("User {} authenticated successfully", username);
        int currentStreak = user.getSteak();
        Instant now = Instant.now();
        user.registerLogin(now);
        userRepository.save(user);
        if(user.getSteak() != currentStreak){
            userLoginEventProducer.send(UserLoginEvent.builder()
                            .eventTime(Instant.now())
                            .userId(userDetails.getId())
                            .username(userDetails.getUsername())
                            .streak(user.getSteak())
                            .received_at(Instant.now())
                            .build());
        }
        return userDetails;
    }

    @Override
    public AppUserDetails getUserInfo(String userId) throws UsernameNotFoundException {
        log.info("Getting user info for userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        log.info("User info retrieved for userId: {}", userId);
        return new AppUserDetails(user);
    }

    private User loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        log.info("User found with username: {}", username);
        return user;
    }

}

