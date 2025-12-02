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
//    List<Session> findByUserId(String userId);
//
//    @Query("SELECT s FROM Session s WHERE s.deck.id = :deckId AND s.userId = :userId")
//    List<Session> findByDeckIdAndUserId(@Param("deckId") String deckId, @Param("userId") String userId);
//
//    @Query("SELECT s FROM Session s WHERE s.userId = :userId AND s.deck.id = :deckId AND s.status = :status")
//    Optional<Session> findByUserIdAndDeckIdAndStatus(
//            @Param("userId") String userId,
//            @Param("deckId") String deckId,
//            @Param("status") SessionStatus status
//    );
//    long countByDeckId(String deckId);
//
//    @Query("SELECT COUNT(s) FROM Session s WHERE s.deck.id = :deckId AND s.status = :status")
//    long countByDeckIdAndStatus(@Param("deckId") String deckId, @Param("status") SessionStatus status);
}
