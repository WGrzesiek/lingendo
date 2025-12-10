package com.learnwords.deckservice.repository;

import com.learnwords.deckservice.entity.Flashcard;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FlashcardRepository extends JpaRepository<Flashcard, String> {
    List<Flashcard> findByDeckId(String deckId);
    Page<List<Flashcard>> findByDeckId(String deckId, Pageable pageable);
    List<Flashcard> findByIdIn(List<String> flashcardIds);


}
