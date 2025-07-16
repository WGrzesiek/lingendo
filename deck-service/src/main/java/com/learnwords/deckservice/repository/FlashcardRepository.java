package com.learnwords.deckservice.repository;

import com.learnwords.deckservice.entity.Flashcard;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FlashcardRepository extends JpaRepository<Flashcard, String> {
    List<Flashcard> findByDeckId(String deckId);

    @Query("SELECT f FROM Flashcard f WHERE f.deck = :deckId AND f.algorithmState <> :maxLimit ORDER BY RAND()")
    List<Flashcard> findByDeckIdOrderByRandomWhereAlgorithmStateIsntMaxLimit(String deckId, Long limit);
}
