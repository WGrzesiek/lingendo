package com.learnwords.deckservice.repository;

import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.enums.DeckOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface DeckRepository extends JpaRepository<Deck, String> {
    boolean existsByNameAndUserId(String name, String userId);
    
    List<Deck> findByUserId(String userId);
    List<Deck> findByUserIdAndIsPublic(String userId, boolean isPublic);
    List<Deck> findByUserIdAndOwner(String userId, DeckOwner owner);
    List<Deck> findByIsPublic(boolean isPublic);
    List<Deck> findByOwner(DeckOwner owner);
    
    long countByUserId(String userId);
    long countByUserIdAndIsPublic(String userId, boolean isPublic);
    
    @Query("SELECT d FROM Deck d WHERE " +
           "(d.userId = :userId) AND " +
           "(:isPublic IS NULL OR d.isPublic = :isPublic) AND " +
           "(:owner IS NULL OR d.owner = :owner)")
    List<Deck> findByFilters(
        @Param("userId") String userId,
        @Param("isPublic") Boolean isPublic,
        @Param("owner") DeckOwner owner
    );

}
