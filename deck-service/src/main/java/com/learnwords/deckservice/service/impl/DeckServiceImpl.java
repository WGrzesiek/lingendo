package com.learnwords.deckservice.service.impl;

import com.learnwords.deckservice.dto.CreateDeckDto;
import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.exception.exceptions.DeckWithThisNameForThisUserAlreadyExistsException;
import com.learnwords.deckservice.repository.DeckRepository;
import com.learnwords.deckservice.service.DeckService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class DeckServiceImpl implements DeckService {
    private final DeckRepository deckRepository;

    public DeckServiceImpl(DeckRepository deckRepository) {
        this.deckRepository = deckRepository;
    }

    @Override
    @Transactional
    public void createDeck(String userId, CreateDeckDto createDeckDto) throws DeckWithThisNameForThisUserAlreadyExistsException {
        log.info("Tworzenie tali: {}", createDeckDto.getDeckName());
        String deckId = UUID.randomUUID().toString();
        try {
            if(deckRepository.existsByNameAndUserId(createDeckDto.getDeckName(), userId)) {
                log.error("Talia o nazwie '{}' już istnieje dla tego usera", createDeckDto.getDeckName());
                throw new DeckWithThisNameForThisUserAlreadyExistsException("Talia o tej nazwie już istnieje dla tego użytkownika");
            }
            deckRepository.save(
                    Deck.builder()
                            .id(deckId)
                            .name(createDeckDto.getDeckName())
                            .description(createDeckDto.getDescription())
                            .userId(userId)
                            .howManyFlashcardsForOneSession(createDeckDto.getHowManyFlashcardsForOneSession())
                            .isPublic(createDeckDto.getIsPublic())
                            .wordCount(0)
                            .learnAlgorithm(createDeckDto.getLearnAlgorithm())
                            .languageFrom(createDeckDto.getLanguageFrom())
                            .languageTo(createDeckDto.getLanguageTo())
                            .build()
            );
            log.info("Talia '{}' została pomyślnie utworzona przez użytkownika {}", createDeckDto.getDeckName(), userId);
        }
        catch (Exception e) {
            log.error("Błąd podczas sprawdzania istnienia talii: {}", e.getMessage());
            throw new RuntimeException("Błąd podczas sprawdzania istnienia talii: {}", e);
        }


    }


    @Override
    public void deleteDeck(String deckId) {

    }
}
