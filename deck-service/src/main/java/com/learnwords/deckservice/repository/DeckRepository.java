package com.learnwords.deckservice.repository;

import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.enums.DeckOwner;
import com.learnwords.deckservice.enums.DeckVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeckRepository extends JpaRepository<Deck, String> {
    boolean existsByNameAndOwnerId(String name, String userId);

    @Query("""
            SELECT d FROM Deck d WHERE
            d.ownerId = :userId AND
            d.visibility IN :visibility AND
            d.owner = :owner
           """)
    Page<Deck> findByFilters(String userId, List<DeckVisibility> visibility, DeckOwner owner, Pageable pageable);

}
