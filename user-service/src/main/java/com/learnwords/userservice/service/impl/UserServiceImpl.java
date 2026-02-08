package com.learnwords.userservice.service.impl;

import com.learnwords.common.KafkaTopic;
import com.learnwords.common.events.UserLoginEvent;
import com.learnwords.userservice.dtos.ChangePasswordRequest;
import com.learnwords.userservice.dtos.RegisterRequest;
import com.learnwords.userservice.dtos.UpdateProfileRequest;
import com.learnwords.userservice.dtos.UserProfileResponse;
import com.learnwords.userservice.entity.User;
import com.learnwords.userservice.events.GenericEventProducer;
import com.learnwords.userservice.exception.exceptions.EmailAlreadyExistsException;
import com.learnwords.userservice.exception.exceptions.UsernameAlreadyExistsException;
import com.learnwords.userservice.exception.exceptions.WrongPasswordException;
import com.learnwords.userservice.repository.UserRepository;
import com.learnwords.userservice.security.AppUserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.learnwords.userservice.service.PasswordService;
import com.learnwords.userservice.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.UUID;


@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final PasswordService passwordService;
    private final UserRepository userRepository;
    private final GenericEventProducer genericEventProducer;


    public UserServiceImpl(PasswordService passwordService, UserRepository userRepository, GenericEventProducer genericEventProducer) {
        this.passwordService = passwordService;
        this.userRepository = userRepository;
        this.genericEventProducer = genericEventProducer;

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
        int currentStreak = user.getStreak();
        Instant now = Instant.now();
        user.registerLogin(now);
        userRepository.save(user);
        if(user.getStreak() != currentStreak){
            genericEventProducer.send(KafkaTopic.USER_LOGINS_TOPIC, UserLoginEvent.builder()
                            .eventTime(Instant.now())
                            .userId(userDetails.getId())
                            .username(userDetails.getUsername())
                            .streak(user.getStreak())
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

    @Override
    public String getUsernameById(String userId) {
        log.info("Getting username for userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        log.info("Username {} retrieved for userId: {}", user.getUsername(), userId);
        return user.getUsername();
    }

    private User loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        log.info("User found with username: {}", username);
        return user;
    }

    @Override
    public UserProfileResponse getProfile(String userId) {
        log.info("Pobieranie profilu dla userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika o id: " + userId));
        
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getUserType(),
                user.getAccountType(),
                user.getCreatedAt(),
                user.getLastLogin(),
                user.getStreak()
        );
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(String userId, UpdateProfileRequest request) {
        log.info("Aktualizacja profilu dla userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika o id: " + userId));

        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new EmailAlreadyExistsException(request.email());
            }
            user.setEmail(request.email());
        }

        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }

        userRepository.save(user);
        log.info("Profil zaktualizowany dla userId: {}", userId);

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getUserType(),
                user.getAccountType(),
                user.getCreatedAt(),
                user.getLastLogin(),
                user.getStreak()
        );
    }

    @Override
    @Transactional
    public void changePassword(String userId, ChangePasswordRequest request) {
        log.info("Zmiana hasła dla userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika o id: " + userId));

        if (!passwordService.matchPassword(request.currentPassword(), user.getPassword())) {
            log.warn("Nieprawidłowe aktualne hasło dla userId: {}", userId);
            throw new WrongPasswordException();
        }

        user.setPassword(passwordService.hashPassword(request.newPassword()));
        user.setLastPasswordChange(Instant.now());
        userRepository.save(user);
        
        log.info("Hasło zmienione pomyślnie dla userId: {}", userId);
    }

}

