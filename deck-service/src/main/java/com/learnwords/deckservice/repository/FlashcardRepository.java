package com.learnwords.deckservice.repository;

import com.learnwords.deckservice.entity.Flashcard;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlashcardRepository extends JpaRepository<Flashcard, String> {
    List<Flashcard> findByDeckId(String deckId);

}
