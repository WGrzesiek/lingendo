package com.learnwords.vocabularyreadservice.service.impl;

import com.learnwords.common.dto.ResponseSentenceDto;
import com.learnwords.vocabularyreadservice.entity.Sentence;
import com.learnwords.vocabularyreadservice.enums.FetchStrategy;
import com.learnwords.vocabularyreadservice.exception.exceptions.InvalidPageSizeException;
import com.learnwords.vocabularyreadservice.exception.exceptions.InvalidSentenceIdException;
import com.learnwords.vocabularyreadservice.exception.exceptions.SentenceNotFoundException;
import com.learnwords.vocabularyreadservice.repository.SentenceRepository;
import com.learnwords.vocabularyreadservice.service.SentenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementacja serwisu zarządzającego przykładowymi zdaniami.
 */
@Slf4j
@Service
public class SentenceServiceImpl implements SentenceService {

    private final SentenceRepository sentenceRepository;

    public SentenceServiceImpl(SentenceRepository sentenceRepository) {
        this.sentenceRepository = sentenceRepository;
    }

    @Override
    public Optional<ResponseSentenceDto> getSentenceById(String id) {
        validateId(id);
        log.info("Pobieranie zdania o id: {}", id);
        return sentenceRepository.findById(id).map(this::mapToDto);
    }

    @Override
    public List<ResponseSentenceDto> getSentencesByIds(List<String> ids) {
        validateIds(ids);
        log.info("Pobieranie {} zdań", ids.size());
        return sentenceRepository.findAllById(ids).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<ResponseSentenceDto> getSentences(int page_size, FetchStrategy fetchStrategy) {
        validatePageSize(page_size);
        log.info("Pobieranie {} zdań, strategia: {}", page_size, fetchStrategy);
        
        List<Sentence> result = switch (fetchStrategy) {
            case RANDOM -> sentenceRepository.findRandom(page_size);
            case Alphabetically -> sentenceRepository.findRandomSortedAlphabetically(page_size);
        };
        
        return result.stream()
                .map(this::mapToDto)
                .toList();
    }

    /**
     * Waliduje pojedyncze ID zdania.
     */
    private void validateId(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidSentenceIdException();
        }
    }

    /**
     * Waliduje listę ID zdań.
     */
    private void validateIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new InvalidSentenceIdException("Lista ID zdań nie może być pusta");
        }
    }

    /**
     * Waliduje rozmiar strony.
     */
    private void validatePageSize(int pageSize) {
        if (pageSize <= 0) {
            throw new InvalidPageSizeException();
        }
    }

    /**
     * Mapuje encję Sentence na DTO.
     */
    private ResponseSentenceDto mapToDto(Sentence sentence) {
        return new ResponseSentenceDto(
                sentence.getId(),
                sentence.getSentence(),
                sentence.getTranslation()
        );
    }
}
