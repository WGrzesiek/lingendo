package com.learnwords.deckservice.repository;

import com.learnwords.deckservice.entity.Session;
import com.learnwords.deckservice.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

/**
 * Repozytorium dla encji Session.
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-12
 */
public interface SessionRepository extends JpaRepository<Session, String> {
    boolean existsByEnrollment_IdAndStatusIn(String enrollmentId, Collection<SessionStatus> statuses);
    int countByEnrollment_Id(String enrollmentId);

}
