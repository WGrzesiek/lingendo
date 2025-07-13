package com.learnwords.deckservice.repository;

import com.learnwords.deckservice.entity.Deck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeckRepository extends JpaRepository<Deck, String> {
    boolean existsByNameAndUserId(String name, String userId);
}
