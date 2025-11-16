package com.learnwords.deckservice.repository;

import com.learnwords.deckservice.entity.SessionFlashcard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repozytorium dla encji SessionFlashcard.
 * 
 * <p>Zarządza relacją między sesjami nauki a fiszkami.
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-12
 */
public interface SessionFlashcardRepository extends JpaRepository<SessionFlashcard, String> {
    
    /**
     * Znajduje wszystkie fiszki przypisane do sesji.
     * 
     * @param sessionId ID sesji
     * @return lista fiszek w sesji
     */
    @Query("SELECT sf FROM SessionFlashcard sf WHERE sf.session.id = :sessionId")
    List<SessionFlashcard> findBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * Znajduje konkretną fiszkę w sesji.
     * 
     * @param sessionId ID sesji
     * @param flashcardId ID fiszki
     * @return Optional z SessionFlashcard jeśli fiszka jest w sesji
     */
    @Query("SELECT sf FROM SessionFlashcard sf WHERE sf.session.id = :sessionId AND sf.flashcard.id = :flashcardId")
    Optional<SessionFlashcard> findBySessionIdAndFlashcardId(
            @Param("sessionId") String sessionId, 
            @Param("flashcardId") String flashcardId
    );
    
    /**
     * Liczy fiszki w sesji.
     * 
     * @param sessionId ID sesji
     * @return liczba fiszek
     */
    @Query("SELECT COUNT(sf) FROM SessionFlashcard sf WHERE sf.session.id = :sessionId")
    int countBySessionId(@Param("sessionId") String sessionId);
}