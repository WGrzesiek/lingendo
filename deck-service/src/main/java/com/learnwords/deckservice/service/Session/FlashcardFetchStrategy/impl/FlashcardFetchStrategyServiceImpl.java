package com.learnwords.deckservice.service.Session.FlashcardFetchStrategy.impl;

import com.learnwords.deckservice.service.GrpcClient.VocabularyGrpcClient;
import com.learnwords.deckservice.entity.Flashcard;
import com.learnwords.deckservice.service.Session.FlashcardFetchStrategy.FlashcardFetchStrategy;
import com.learnwords.deckservice.service.Session.FlashcardFetchStrategy.FlashcardFetchStrategyService;
import com.learnwords.common.dto.OnlyWordDto;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class FlashcardFetchStrategyServiceImpl implements FlashcardFetchStrategyService {

    private final VocabularyGrpcClient vocabularyClient;

    public FlashcardFetchStrategyServiceImpl (
            VocabularyGrpcClient vocabularyClient){
        this.vocabularyClient = vocabularyClient;
    }


    public List<Flashcard> sortFlashcardsByStrategy(FlashcardFetchStrategy strategy, Long limit, List<Flashcard> flashcards){
        var ids = flashcards.stream().map(Flashcard::getWordId).toList();
        return switch (strategy) {
            case ALPHABETICAL -> {
                List<Flashcard> result = new ArrayList<>();
                try {
//                    var batchGetOnlyWord = vocabularyClient.batchGetOnlyWord(ids);
                    var batchGetOnlyWord = vocabularyClient.batchGetVocabularies(ids);
                    List<OnlyWordDto> wordDtos = new ArrayList<>();
                    if (batchGetOnlyWord.getVocabulariesCount() > 0) {
                        for (var word : batchGetOnlyWord.getVocabulariesList()) {
                            wordDtos.add(new OnlyWordDto(word.getId(), word.getWord()));
                        }
                    }
                    wordDtos.sort((o1, o2) -> o1.word().compareToIgnoreCase(o2.word()));
                    for (OnlyWordDto word : wordDtos) {
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

            } catch (StatusRuntimeException e) {
                switch (e.getStatus().getCode()) {
                    case INVALID_ARGUMENT -> { /* walidacja wejścia; log + przerwij */ }
                    case DEADLINE_EXCEEDED -> { /* fallback: zwróć nieposortowane lub przerwij */ }
                    case UNAVAILABLE -> { /* krótki retry + fallback */ }
                    default -> { /* INTERNAL/UNKNOWN: log + fallback/przerwij */ }
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


