package com.learnwords.deckservice.repository;

import com.learnwords.deckservice.entity.Session;
import com.learnwords.deckservice.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repozytorium dla encji Session.
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-12
 */
public interface SessionRepository extends JpaRepository<Session, String> {
    
    /**
     * Znajduje wszystkie sesje użytkownika.
     * 
     * @param userId ID użytkownika
     * @return lista sesji użytkownika
     */
    List<Session> findByUserId(String userId);
    
    /**
     * Znajduje wszystkie sesje dla talii.
     * 
     * @param deckId ID talii
     * @return lista sesji talii
     */
    @Query("SELECT s FROM Session s WHERE s.deck.id = :deckId")
    List<Session> findByDeckId(@Param("deckId") String deckId);
    
    /**
     * Znajduje aktywną sesję użytkownika dla talii.
     * 
     * @param userId ID użytkownika
     * @param deckId ID talii
     * @param status status sesji (IN_PROGRESS)
     * @return Optional z aktywną sesją jeśli istnieje
     */
    @Query("SELECT s FROM Session s WHERE s.userId = :userId AND s.deck.id = :deckId AND s.status = :status")
    Optional<Session> findByUserIdAndDeckIdAndStatus(
            @Param("userId") String userId, 
            @Param("deckId") String deckId,
            @Param("status") SessionStatus status
    );
}
