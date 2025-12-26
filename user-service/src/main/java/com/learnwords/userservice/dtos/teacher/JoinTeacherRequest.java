package com.learnwords.userservice.dtos.teacher;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO żądania dołączenia do nauczyciela za pomocą kodu zaproszenia
 *
 * @param invitationCode kod zaproszenia
 */
public record JoinTeacherRequest(
        @NotBlank(message = "Kod zaproszenia jest wymagany")
        @Size(min = 6, max = 32, message = "Kod zaproszenia musi mieć od 6 do 32 znaków")
        String invitationCode
) {
}
