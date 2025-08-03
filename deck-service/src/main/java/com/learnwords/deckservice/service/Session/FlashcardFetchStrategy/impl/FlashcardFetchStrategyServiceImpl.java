package com.learnwords.deckservice.service.Session.FlashcardFetchStrategy.impl;

import com.learnwords.deckservice.entity.Flashcard;
import com.learnwords.deckservice.feignClient.VocabularyClient;
import com.learnwords.deckservice.repository.FlashcardRepository;
import com.learnwords.deckservice.service.Session.FlashcardFetchStrategy.FlashcardFetchStrategy;
import com.learnwords.deckservice.service.Session.FlashcardFetchStrategy.FlashcardFetchStrategyService;
import com.learnwords.common.dto.OnlyWordDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FlashcardFetchStrategyServiceImpl implements FlashcardFetchStrategyService {

    private final FlashcardRepository flashcardRepository;
    private final VocabularyClient vocabularyClient;

    public FlashcardFetchStrategyServiceImpl (
            FlashcardRepository flashcardRepository, VocabularyClient vocabularyClient) {
        this.flashcardRepository = flashcardRepository;
        this.vocabularyClient = vocabularyClient;
    }

    public List<Flashcard> sortFlashcardsByStrategy(FlashcardFetchStrategy strategy, Long limit, List<Flashcard> flashcards){
        return switch (strategy) {
            case ALPHABETICAL -> {
                List<OnlyWordDto> words = vocabularyClient.getWords(flashcards.stream().map(Flashcard::getWordId).toList());
                // Sortuj słowa alfabetycznie
                words.sort((o1, o2) -> o1.word().compareToIgnoreCase(o2.word()));

                // Przygotuj posortowaną listę fiszek na podstawie posortowanych słów
                List<Flashcard> result = new ArrayList<>();
                for (OnlyWordDto word : words) {
                    for (Flashcard flashcard : flashcards) {
                        if (flashcard.getWordId().equals(word.id())) {
                            result.add(flashcard);
                            break;
                        }
                    }
                    if (result.size() >= limit) {
                        break;
                    }
                }
                yield result;
            }

            case RANDOM -> null;
            case REVERSE_ALPHABETICAL -> null;
            case UNLEARNED_FIRST -> null;
        };
        }
    }


