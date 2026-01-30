package com.learnwords.userservice.dtos.friendship;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO żądania wysłania zaproszenia do znajomych
 *
 * @param targetUserId identyfikator użytkownika, którego chcemy zaprosić
 */
public record SendFriendRequestDto(
        @NotBlank(message = "ID użytkownika jest wymagane")
        String targetUserId
) {
}
