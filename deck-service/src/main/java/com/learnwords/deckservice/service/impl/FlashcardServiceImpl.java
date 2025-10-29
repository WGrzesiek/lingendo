package com.learnwords.deckservice.service.impl;

import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.dto.VocabularyDto;
import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.entity.Flashcard;
import com.learnwords.deckservice.enums.LearnAlgorithm;
import com.learnwords.deckservice.repository.DeckRepository;
import com.learnwords.deckservice.repository.FlashcardRepository;
import com.learnwords.deckservice.service.Algorithm.GrzesiekAlgorithm;
import com.learnwords.deckservice.service.FlashcardService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class FlashcardServiceImpl implements FlashcardService {

    private final FlashcardRepository flashcardRepository;
    private final DeckRepository deckRepository;
    private final GrzesiekAlgorithm grzesiekAlgorithm;


    public FlashcardServiceImpl(FlashcardRepository flashcardRepository, DeckRepository deckRepository, GrzesiekAlgorithm grzesiekAlgorithm) {
        this.flashcardRepository = flashcardRepository;
        this.deckRepository = deckRepository;
        this.grzesiekAlgorithm = grzesiekAlgorithm;
    }

    @Override
    @Transactional
    @KafkaListener(topics = KafkaTopic.CREATE_VOCABULARY_TOPIC, groupId = KafkaGroup.DECK_SERVICE_GROUP, properties = {
            "spring.json.value.default.type=com.learnwords.common.dto.VocabularyDto"
    })
    public void processFlashcardCreate(VocabularyDto vocabularyDto) {
        log.info("Otrzymano event: {}", KafkaTopic.CREATE_VOCABULARY_TOPIC);
        String flashcardId = UUID.randomUUID().toString();
        Flashcard flashcard = new Flashcard();

        try {
            Deck deck = deckRepository.findById(vocabularyDto.deckId())
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono talii o id: " + vocabularyDto.deckId()));

            flashcard.setId(flashcardId);
            flashcard.setWordId(vocabularyDto.id());
            flashcard.setDeck(deck);
            setInitialFlashcardState(vocabularyDto.deckId(), flashcard);
            flashcardRepository.save(flashcard);
            log.info("Zapisano fiszke o id: {}", flashcardId);
//            deck.setWordCount(deck.getWordCount() + 1);
            deck.setWordCount(deck.getFlashcards().size());
            deckRepository.save(deck);
            log.info("Zaktualizowano talię o id: {}", vocabularyDto.deckId());
        }
        catch (DataAccessException e) {
            log.error("Błąd dostępu do bazy danych: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd dostępu do bazy danych: " + e.getMessage(), e);
        }
        catch (Exception e) {
            log.error("Błąd podczas tworzenia fiszki: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd podczas tworzenia fiszki: " + e.getMessage(), e);
        }
    }

    @Override
    public void setInitialFlashcardState(String deckId, Flashcard flashcard) {
        try{
            LearnAlgorithm algorithm = deckRepository.findById(deckId)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono talii o id: " + deckId))
                    .getLearnAlgorithm();
            switch (algorithm) {
                case GRZESIEK_ALGORITHM -> flashcard.setAlgorithmState(grzesiekAlgorithm.initialize().serialize());
            }
            log.info("Ustawianie początkowego stanu fiszki dla talii: {}", deckId);
        }
        catch (DataAccessException e) {
            log.error("Błąd dostępu do bazy danych: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd dostępu do bazy danych: " + e.getMessage(), e);
        }
        catch (Exception e) {
            log.error("Błąd podczas ustawiania początkowego stanu fiszki: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd podczas ustawiania początkowego stanu fiszki: " + e.getMessage(), e);
        }
    }
}
