package com.learnwords.deckservice.service.impl;

import com.learnwords.deckservice.service.grpcClient.VocabularyGrpcClient;
import com.learnwords.deckservice.entity.Flashcard;
import com.learnwords.deckservice.service.FlashcardFetchStrategy;
import com.learnwords.deckservice.service.FlashcardFetchStrategyService;
import com.learnwords.common.dto.OnlyWordDto;
import io.grpc.StatusRuntimeException;
import org.apache.kafka.common.protocol.types.Field;
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
        List<String> ids = flashcards.stream().map(Flashcard::getWordId).toList();
        return switch (strategy) {
            case ALPHABETICAL -> {
                List<Flashcard> result = new ArrayList<>();
                try {
                    var batchGetOnlyWord = vocabularyClient.batchGetOnlyWord(ids);
                    List<OnlyWordDto> wordDtos = new ArrayList<>();
                    if (batchGetOnlyWord.getWordCount() != 0) {
                        for (var word : batchGetOnlyWord.getWordList()) {
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
                    case INVALID_ARGUMENT -> {}
                    case DEADLINE_EXCEEDED -> {}
                    case UNAVAILABLE -> {}
                    default -> {}
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


