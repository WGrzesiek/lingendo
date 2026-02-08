package com.learnwords.deckservice.repository;

import com.learnwords.deckservice.entity.DeckEnrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeckEnrollmentRepository extends JpaRepository<DeckEnrollment, String> {
    void deleteByDeckIdAndUserId(String deckId, String userId);
    Page<DeckEnrollment> findAllByUserId(String userId, Pageable pageable);
    Optional<DeckEnrollment> findByIdAndUserId(String deckId, String userId);
}
