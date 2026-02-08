package com.learnwords.deckservice.repository;

import com.learnwords.deckservice.dto.session.FlashcardSessionNumber;
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
 * @version 2.0
 * @since 2025-11-12
 */
public interface SessionFlashcardRepository extends JpaRepository<SessionFlashcard, String> {

    List<SessionFlashcard> findBySession_Id(String sessionId);
    @Query("""
    select new com.learnwords.deckservice.dto.session.FlashcardSessionNumber(
        sf.flashcard.id,
        sf.session.sessionNumber
    )
    from SessionFlashcard sf
    where sf.flashcard.id in :flashcardIds
""")
    List<FlashcardSessionNumber> getFlashcardSessionNumbersByIds(List<String> flashcardIds);

}