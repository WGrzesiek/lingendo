package com.learnwords.deckservice.repository;

import com.learnwords.deckservice.entity.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlashcardRepository extends JpaRepository<Flashcard, String> {
}
