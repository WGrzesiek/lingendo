package com.learnwords.deckservice.repository;

import com.learnwords.deckservice.entity.SessionFlashcard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionFlashcardRepository extends JpaRepository<SessionFlashcard, String> {
}