package com.learnwords.vocabularycommandservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CreateSentenceDto {

    @NotBlank(message = "Wypelnij")
    private String sentence;

    @NotBlank(message = "Wypelnij")
    private String translation;
}
