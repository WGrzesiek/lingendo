package com.learnwords.deckservice.dto.course;

import com.learnwords.deckservice.enums.DeckOwner;
import com.learnwords.deckservice.enums.DeckVisibility;
import lombok.Builder;

@Builder
public record CourseHeaderInfo(
    String deckId,
    String name,
    String description,
    String ownerId,
    String username,
    DeckOwner ownerType,
    DeckVisibility visibility,
    String languageFrom,
    String languageTo
) {

}
