package com.learnwords.deckservice.repository;

import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.enums.DeckOwner;
import com.learnwords.deckservice.enums.DeckVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeckRepository extends JpaRepository<Deck, String> {
    boolean existsByNameAndOwnerId(String name, String userId);

 @Query("""
            SELECT d FROM Deck d WHERE
            d.ownerId = :userId
            AND (:visibility IS NULL OR d.visibility IN :visibility)
            AND (:owner IS NULL OR d.owner IN :owner)
           """)
    Page<Deck> findOwnedDecksWithFilters(
            @Param("userId") String userId,
            @Param("visibility") List<DeckVisibility> visibility,
            @Param("owner") List<DeckOwner> owner,
            Pageable pageable);

Page<Deck> findByIdIn(List<String> ids, Pageable pageable);

    @Query("""
            SELECT d FROM Deck d WHERE
            d.visibility = 'PUBLIC'
            AND (:owner IS NULL OR d.owner = :owner)
           """)
    Page<Deck> findPublicDecks(@Param("owner") DeckOwner owner, Pageable pageable);
}
