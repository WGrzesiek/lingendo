package com.learnwords.userservice.service;

import com.learnwords.userservice.security.AppUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AppUserDetailService {
    AppUserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    AppUserDetails loadUserByEmail(String email) throws UsernameNotFoundException;
}