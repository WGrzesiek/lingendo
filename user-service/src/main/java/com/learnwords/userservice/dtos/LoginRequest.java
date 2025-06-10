package com.learnwords.userservice.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {
    @Valid

    @NotBlank(message = "Nazwa użytkownika jest wymagana")
    @NotNull(message = "Nazwa użytkownika nie może być pusta")
    private String username;

    @NotBlank(message = "Hasło jest wymagane")
    @NotNull(message = "Hasło nie może być puste")
    private String password;
}
