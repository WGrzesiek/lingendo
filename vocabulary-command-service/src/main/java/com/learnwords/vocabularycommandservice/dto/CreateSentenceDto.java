package com.learnwords.vocabularycommandservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class CreateSentenceDto {
    @Valid

    @NotBlank(message = "Wypelnij")
    @NotNull(message = "Wypelnij")
    private String sentence;

    @NotBlank(message = "Wypelnij")
    @NotNull(message = "Wypelnij")
    private String translation;
}
