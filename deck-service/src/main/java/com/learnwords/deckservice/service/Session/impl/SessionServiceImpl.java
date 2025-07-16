package com.learnwords.deckservice.service.Session.impl;

import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.entity.Session;
import com.learnwords.deckservice.repository.DeckRepository;
import com.learnwords.deckservice.repository.SessionRepository;
import com.learnwords.deckservice.service.Session.FlashcardFetchStrategy.FlashcardFetchStrategy;
import com.learnwords.deckservice.service.Session.SessionFlashcardService;
import com.learnwords.deckservice.service.Session.SessionService;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class SessionServiceImpl implements SessionService {

    private final DeckRepository deckRepository;
    private final SessionRepository sessionRepository;
    private final SessionFlashcardService sessionFlashcardService;

    public SessionServiceImpl(DeckRepository deckRepository, SessionRepository sessionRepository, SessionFlashcardService sessionFlashcardService) {
        this.deckRepository = deckRepository;
        this.sessionRepository = sessionRepository;
        this.sessionFlashcardService = sessionFlashcardService;

    }

    @Override
//    @Transactional
    public String initializeSession(String deckId, FlashcardFetchStrategy flashcardFetchStrategy) {
        log.info("Inicjalizacja sesji dla talii: {}", deckId);
        try {
            Deck deck = deckRepository.findById(deckId)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono talii o id: " + deckId));
            Session session = Session.builder()
                    .id(UUID.randomUUID().toString())
                    .deck(deck)
                    .build();
            sessionRepository.save(session);
            sessionFlashcardService.addFlashcardsToSession(session, deck, flashcardFetchStrategy);
            return session.getId();
        }catch (DataAccessException e) {
            log.error("Błąd dostępu do danych podczas dodawania fiszki do sesji: {}", e.getMessage());
            throw new RuntimeException("Błąd dostępu do danych: " + e.getMessage());
        }
        catch (Exception e) {
            log.error("Błąd podczas inicjalizacji sesji: {}", e.getMessage());
            throw new RuntimeException("Błąd podczas inicjalizacji sesji: " + e.getMessage());
        }
    }

    }
