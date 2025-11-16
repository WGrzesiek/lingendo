package com.learnwords.vocabularyreadservice.service.impl;

import com.learnwords.common.dto.OnlyWordDto;
import com.learnwords.common.dto.SentenceDto;
import com.learnwords.common.dto.WordDto;
import com.learnwords.vocabularyreadservice.exception.exceptions.InvalidVocabularyIdException;
import com.learnwords.vocabularyreadservice.entity.Vocabulary;
import com.learnwords.vocabularyreadservice.repository.SentenceAIRepository;
import com.learnwords.vocabularyreadservice.repository.SentenceRepository;
import com.learnwords.vocabularyreadservice.repository.VocabularyRepository;
import com.learnwords.vocabularyreadservice.service.VocabularyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementacja serwisu zarządzającego słownictwem (vocabulary).
 * 
 * <p>Serwis odpowiedzialny za operacje odczytu słów, tłumaczeń i przykładowych zdań
 * z bazy danych. Udostępnia różne warianty pobierania danych w zależności od potrzeb.
 * 
 * <p>Główne funkcjonalności:
 * <ul>
 *   <li>Pobieranie pełnych danych słów z tłumaczeniami i zdaniami ({@link WordDto})</li>
 *   <li>Pobieranie minimalnych danych słów ({@link OnlyWordDto})</li>
 *   <li>Obsługa zarówno pojedynczych słów jak i list słów</li>
 *   <li>Integracja ze zdaniami zwykłymi i generowanymi przez AI</li>
 * </ul>
 * 
 * @author Grzegorz Wawrzeń
 * @version 2.0
 * @since 2025-11-11
 * @see VocabularyService
 * @see WordDto
 * @see OnlyWordDto
 */
@Slf4j
@Service
public class VocabularyServiceImpl implements VocabularyService {

    private final VocabularyRepository vocabularyRepository;
    private final SentenceRepository sentenceRepository;
    private final SentenceAIRepository sentenceAIRepository;

    public VocabularyServiceImpl(VocabularyRepository vocabularyRepository, SentenceRepository sentenceRepository, SentenceAIRepository sentenceAIRepository) {
        this.sentenceRepository = sentenceRepository;
        this.vocabularyRepository = vocabularyRepository;
        this.sentenceAIRepository = sentenceAIRepository;
    }

    /**
     * Pobiera minimalne dane słów (tylko ID i słowo) według listy ID.
     * 
     * @param ids Lista ID słów do pobrania
     * @return Lista OnlyWordDto zawierająca tylko ID i słowo
     * @throws InvalidVocabularyIdException gdy lista ids jest null lub pusta
     */
    public List<OnlyWordDto> getOnlyWordsByIds(List<String> ids) {
        validateIds(ids);
        log.info("Pobieranie {} minimalnych słów", ids.size());
        return vocabularyRepository.findAllById(ids).stream()
                .map(v -> new OnlyWordDto(v.getId(), v.getWord()))
                .toList();
    }

    /**
     * Pobiera pełne dane słowa według ID.
     * 
     * @param id ID słowa do pobrania
     * @return Optional zawierający WordDto z pełnymi danymi słowa
     * @throws InvalidVocabularyIdException gdy ID jest null lub puste
     * @see WordDto
     * @see SentenceDto
     */
    @Override
    public Optional<WordDto> getWordById(String id) {
        validateId(id);
        log.info("Pobieranie pełnego słowa o id: {}", id);
        return vocabularyRepository.findById(id).map(this::mapToWordDto);
    }

    /**
     * Pobiera pełne dane wielu słów według listy ID.
     * 
     * @param ids Lista ID słów do pobrania
     * @return Lista WordDto z pełnymi danymi słów
     * @throws InvalidVocabularyIdException gdy lista ids jest null lub pusta
     * @see WordDto
     * @see SentenceDto
     */
    @Override
    public List<WordDto> getWordsByIds(List<String> ids) {
        validateIds(ids);
        log.info("Pobieranie {} pełnych słów", ids.size());
        return vocabularyRepository.findAllById(ids).stream()
                .map(this::mapToWordDto)
                .toList();
    }

    /**
     * Waliduje pojedyncze ID słownictwa.
     */
    private void validateId(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidVocabularyIdException();
        }
    }

    /**
     * Waliduje listę ID słownictwa.
     */
    private void validateIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new InvalidVocabularyIdException("Lista ID słownictwa nie może być pusta");
        }
    }

    /**
     * Mapuje encję Vocabulary na DTO WordDto z pełnymi danymi.
     */
    private WordDto mapToWordDto(Vocabulary vocabulary) {
        List<SentenceDto> sentences = fetchSentences(vocabulary.getSentenceIds());
        List<SentenceDto> sentencesAI = fetchSentencesAI(vocabulary.getSentenceAIds());

        return new WordDto(
                vocabulary.getId(),
                vocabulary.getWord(),
                vocabulary.getTranslations(),
                sentences,
                sentencesAI
        );
    }

    /**
     * Pobiera zwykłe zdania przykładowe według listy ID.
     */
    private List<SentenceDto> fetchSentences(List<String> sentenceIds) {
        if (sentenceIds == null || sentenceIds.isEmpty()) {
            return List.of();
        }
        return sentenceRepository.findAllById(sentenceIds)
                .stream()
                .map(s -> new SentenceDto(s.getId(), s.getSentence(), s.getTranslation()))
                .toList();
    }

    /**
     * Pobiera zdania wygenerowane przez AI według listy ID.
     */
    private List<SentenceDto> fetchSentencesAI(List<String> sentenceAIds) {
        if (sentenceAIds == null || sentenceAIds.isEmpty()) {
            return List.of();
        }
        return sentenceAIRepository.findAllById(sentenceAIds)
                .stream()
                .map(s -> new SentenceDto(s.getId(), s.getSentenceAI(), s.getTranslationAI()))
                .toList();
    }
}

