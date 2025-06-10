package com.learnwords.userservice.integration;

import com.learnwords.userservice.TestDataUtil;
import com.learnwords.userservice.dtos.RegisterRequest;
import com.learnwords.userservice.entity.User;
import com.learnwords.userservice.exception.exceptions.EmailAlreadyExistsException;
import com.learnwords.userservice.exception.exceptions.UsernameAlreadyExistsException;
import com.learnwords.userservice.repository.UserRepository;
import com.learnwords.userservice.service.PasswordService;
import com.learnwords.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordService passwordService;
    @InjectMocks
    UserServiceImpl userService;

    @Test
    void shouldRegisterUser() {
        RegisterRequest request = TestDataUtil.registerRequest();
        userService.registerUser(request, "UUID-1");
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        then(userRepository).should().save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getUsername()).isEqualTo("testUser");
        assertThat(capturedUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        RegisterRequest request = TestDataUtil.registerRequest();
        userService.registerUser(request, "UUID-1");
        given(userRepository.existsByUsername(request.getUsername())).willReturn(true);
        assertThatThrownBy(() -> userService.registerUser(request, "UUID-1"))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessageContaining("Username '" + request.getUsername() + "' is already taken");
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists(){
        RegisterRequest request = TestDataUtil.registerRequest();
        userService.registerUser(request, "UUID-1");
        given(userRepository.existsByEmail(request.getEmail())).willReturn(true);
        assertThatThrownBy(() -> userService.registerUser(request,"UUID-1"))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("Email '" + request.getEmail() + "' is already taken");
    }

}
