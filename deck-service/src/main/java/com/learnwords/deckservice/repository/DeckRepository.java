package com.learnwords.deckservice.repository;

import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.enums.DeckOwner;
import com.learnwords.deckservice.enums.DeckVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface DeckRepository extends JpaRepository<Deck, String> {
    boolean existsByNameAndUserId(String name, String userId);
    
    List<Deck> findByUserId(String userId);
    Page<Deck> findByUserId(String userId, Pageable pageable);
    List<Deck> findByUserIdAndIsPublic(String userId, boolean isPublic);
    List<Deck> findByUserIdAndOwner(String userId, DeckOwner owner);
    List<Deck> findByIsPublic(boolean isPublic);
    List<Deck> findByOwner(DeckOwner owner);
    
    long countByUserId(String userId);
    long countByUserIdAndIsPublic(String userId, boolean isPublic);
    
    @Query("SELECT d FROM Deck d WHERE " +
           "(d.ownerId = :userId) AND " +
           "(:isPublic IS NULL OR d.visibility = :visibility) AND " +
           "(:owner IS NULL OR d.owner = :owner)")
    List<Deck> findByFilters(
        @Param("userId") String userId,
        @Param("visibility") DeckVisibility visibility,
        @Param("owner") DeckOwner owner
    );

}
