package com.learnwords.vocabularycommandservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateWordDto {
    @Valid

    @NotNull(message = "To pole jest wymagane")
    @NotBlank(message = "To pole jest wymagane")
    private String word;

    @NotNull(message = "Podaj przynajmniej jedno tłumaczenie")
    @Size(min = 1, message = "Podaj przynajmniej jedno tłumaczenie")
    private List<String> translations;

    private List<CreateSentenceDto> sentences;

}
