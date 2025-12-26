package com.learnwords.deckservice.dto.share;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO żądania wycofania udostępnienia.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevokeDeckShareRequest {

    @NotBlank(message = "ID udostępnienia jest wymagane")
    private String shareId;
}
