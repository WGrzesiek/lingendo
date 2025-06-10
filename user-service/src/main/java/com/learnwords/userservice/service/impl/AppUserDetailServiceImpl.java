package com.learnwords.userservice.service.impl;


import com.learnwords.userservice.entity.User;
import com.learnwords.userservice.repository.UserRepository;
import com.learnwords.userservice.security.AppUserDetails;
import com.learnwords.userservice.service.AppUserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@Slf4j
@Service
public class AppUserDetailServiceImpl implements AppUserDetailService {

    private final UserRepository userRepository;

    public AppUserDetailServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AppUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        if (user == null) {
            log.info("User not found with username: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }
        log.info("User found with username: {}", username);
        return new AppUserDetails(user);
    }

    @Override
    public AppUserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        log.info("Loading user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        if (user == null) {
            log.info("User not found with email: {}", email);
            throw new UsernameNotFoundException("User not found: " + email);
        }
        log.info("User found with email: {}", email);
        return new AppUserDetails(user);
    }



}
