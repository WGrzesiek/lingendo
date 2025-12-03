package com.learnwords.deckservice.service.evaluationService;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.learnwords.deckservice.service.algorithm.step.GrzesiekStep;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextAnswer.class, name = "text"),
        @JsonSubTypes.Type(value = ChoiceAnswer.class, name = "choice"),
        @JsonSubTypes.Type(value = RememberedAnswer.class, name = "remembered")
})
public sealed interface UserAnswer permits TextAnswer, ChoiceAnswer, RememberedAnswer {
}

