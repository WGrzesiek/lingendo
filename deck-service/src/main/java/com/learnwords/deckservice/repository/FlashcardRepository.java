package com.learnwords.deckservice.repository;

import com.learnwords.deckservice.entity.Flashcard;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FlashcardRepository extends JpaRepository<Flashcard, String> {
    List<Flashcard> findByDeckId(String deckId);

    @Query("SELECT f FROM Flashcard f WHERE " +
            "(:deckId IS NULL OR f.deck.id = :deckId) AND " +
            "(:isLearned IS NULL OR f.isLearned = :isLearned) AND " +
            "(:isSkipped IS NULL OR f.isSkipped = :isSkipped)")
    List<Flashcard> findByFilters(
            @Param("deckId") String deckId,
            @Param("isLearned") Boolean isLearned,
            @Param("isSkipped") Boolean isSkipped);


}
