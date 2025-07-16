package com.learnwords.deckservice.service.Session.impl;

import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.entity.Flashcard;
import com.learnwords.deckservice.entity.Session;
import com.learnwords.deckservice.entity.SessionFlashcard;
import com.learnwords.deckservice.repository.SessionFlashcardRepository;
import com.learnwords.deckservice.service.Algorithm.AbstractAlgorithm;
import com.learnwords.deckservice.service.Algorithm.Algorithm;
import com.learnwords.deckservice.service.Algorithm.AlgorithmFactory;
import com.learnwords.deckservice.service.Session.FlashcardFetchStrategy.FlashcardFetchStrategy;
import com.learnwords.deckservice.service.Session.FlashcardFetchStrategy.impl.FlashcardFetchStrategyServiceImpl;
import com.learnwords.deckservice.service.Session.SessionFlashcardService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.io.IO.println;

@Slf4j
@Service
public class SessionFlashcardServiceImpl implements SessionFlashcardService {

    private final SessionFlashcardRepository sessionFlashcardRepository;
    private final AlgorithmFactory algorithmFactory;
    private final FlashcardFetchStrategyServiceImpl flashcardFetchStrategyServiceImpl;

    public SessionFlashcardServiceImpl(SessionFlashcardRepository sessionFlashcardRepository, AlgorithmFactory algorithmFactory, FlashcardFetchStrategyServiceImpl flashcardFetchStrategyServiceImpl) {
        this.sessionFlashcardRepository = sessionFlashcardRepository;
        this.algorithmFactory = algorithmFactory;
        this.flashcardFetchStrategyServiceImpl = flashcardFetchStrategyServiceImpl;
    }

    @Override
//    @Transactional
//    @Transactional(propagation = Propagation.REQUIRED)

    public String addFlashcardsToSession(Session session, Deck deck, FlashcardFetchStrategy flashcardFetchStrategy) {
        log.info("Dodawanie {} fiszek do sesji: {}", deck.getFlashcards().size(), session.getId());
        AbstractAlgorithm algorithm = algorithmFactory.get(deck.getLearnAlgorithm());
        try {
            Set<Flashcard> filteredFlashcards = deck.getFlashcards().stream()
                    .filter(flashcard -> !algorithm.deserialize(flashcard.getAlgorithmState()).getStep().isMaxLevel())
                    .collect(Collectors.toSet());
            List<Flashcard> filteredFlashcardsList = new ArrayList<>(filteredFlashcards);

            List<Flashcard> sortedFlashcards = flashcardFetchStrategyServiceImpl.sortFlashcardsByStrategy(
                    flashcardFetchStrategy,
                    deck.getHowManyFlashcardsForOneSession(),
                    filteredFlashcardsList);
            log.error("filteredFlashcards size: {}", filteredFlashcards.size());
            log.error("strategy: {}", flashcardFetchStrategy);
            log.error("limit: {}", deck.getHowManyFlashcardsForOneSession());
            log.error("sortedFlashcards size: {}", sortedFlashcards != null ? sortedFlashcards.size() : "NULL");

            if (sortedFlashcards == null || sortedFlashcards.isEmpty()) {
                log.error("Brak fiszek po sortowaniu - sprawdź implementację sortFlashcardsByStrategy");
                throw new RuntimeException("Nie udało się posortować fiszek");
            }

            Set<SessionFlashcard> sessionFlashcards = sortedFlashcards.stream()
                    .map(flashcard -> SessionFlashcard.builder()
                            .id(UUID.randomUUID().toString())
                            .session(session)
                            .flashcard(flashcard)
                            .build())
                    .collect(Collectors.toSet());
            for (SessionFlashcard sessionFlashcard : sessionFlashcards) {
                println("Dodawanie fiszki do sesji: " + sessionFlashcard.getFlashcard().getId() + " w sesji: " + session.getId());
            }
            sessionFlashcardRepository.saveAll(sessionFlashcards);
            log.info("Pomyślnie dodano {} fiszek do sesji {}", deck.getFlashcards().size(), session.getId());
            return session.getId();
        }catch (DataAccessException e) {
            log.error("Błąd dostępu do danych podczas dodawania fiszki do sesji: {}", e.getMessage());
            throw new RuntimeException("Błąd dostępu do danych: " + e.getMessage());
            }
        catch (Exception e) {
            log.error("Błąd podczas dodawania fiszki do sesji: {}", e.getMessage());
            throw new RuntimeException("Nie udało się dodać fiszki do sesji: " + e.getMessage());
        }
    }

}
