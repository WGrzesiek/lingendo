package com.learnwords.vocabularyreadservice.service.impl;

import com.learnwords.common.dto.OnlyWordDto;
import com.learnwords.common.dto.ResponseVocabularyDto;
import com.learnwords.common.dto.SentenceDto;
import com.learnwords.common.dto.WordDto;
import com.learnwords.vocabularyreadservice.exception.exceptions.SentenceNotFoundException;
import com.learnwords.vocabularyreadservice.exception.exceptions.VocabularyNotFoundException;
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
 * <p><b>Uwaga:</b> Starsze metody zwracające {@link ResponseVocabularyDto} są oznaczone
 * jako deprecated. Preferuj użycie metod zwracających {@link WordDto}.
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
     * Pobiera słownictwo według ID (stara wersja).
     * 
     * @param id ID słowa do pobrania
     * @return Optional zawierający ResponseVocabularyDto z podstawowymi danymi słowa
     * @throws VocabularyNotFoundException gdy ID jest null lub puste
     * @deprecated Od wersji 2.0. Użyj {@link #getWordById(String)} zamiast tego.
     *             Ta metoda zwraca tylko ID zdań zamiast pełnych obiektów.
     */
    @Deprecated(since = "2.0", forRemoval = true)
    public Optional<ResponseVocabularyDto> getVocabularyById(String id) {
        if (id == null || id.isBlank())
            throw new VocabularyNotFoundException(id);
        log.info("Pobieranie słowa o id: {}", id);
        return vocabularyRepository.findById(id).map(vocabulary -> new ResponseVocabularyDto(vocabulary.getId(), vocabulary.getWord(), vocabulary.getTranslations(), vocabulary.getSentenceIds()));
    }

    /**
     * Pobiera listę słownictwa według listy ID (stara wersja).
     * 
     * @param ids Lista ID słów do pobrania
     * @return Lista ResponseVocabularyDto z podstawowymi danymi słów
     * @throws IllegalArgumentException gdy lista ids jest null lub pusta
     * @deprecated Od wersji 2.0. Użyj {@link #getWordsByIds(List)} zamiast tego.
     *             Ta metoda zwraca tylko ID zdań zamiast pełnych obiektów.
     */
    @Deprecated(since = "2.0", forRemoval = true)
    public List<ResponseVocabularyDto> getVocabulariesByIds(List<String> ids) {
        if(ids == null || ids.isEmpty())
            throw new IllegalArgumentException("ids must not be blank");
        log.info("Pobieranie słów o id: {}", ids);
        return vocabularyRepository.findAllById(ids).stream()
                .map(vocabulary -> new ResponseVocabularyDto(vocabulary.getId(),vocabulary.getWord(), vocabulary.getTranslations(), vocabulary.getSentenceIds()))
                .toList();
    }

    /**
     * Pobiera minimalne dane słów (tylko ID i słowo) według listy ID.
     * 
     * <p>Ta metoda jest przydatna gdy potrzebne są tylko podstawowe informacje
     * o słowach bez tłumaczeń i przykładowych zdań (lepsza wydajność).
     * 
     * @param ids Lista ID słów do pobrania
     * @return Lista OnlyWordDto zawierająca tylko ID i słowo
     * @throws IllegalArgumentException gdy lista ids jest null lub pusta
     * @deprecated Od wersji 2.0. Ta metoda pozostanie, ale preferuj użycie {@link #getWordsByIds(List)}
     *             dla pełnych danych lub pozostaw tę metodę dla lekkich zapytań.
     */
    @Deprecated(since = "2.0", forRemoval = false)
    public List<OnlyWordDto> getOnlyWordsByIds(List<String> ids) {
        if (ids == null || ids.isEmpty())
            throw new IllegalArgumentException("id must not be blank");
        log.info("Pobieranie słów o id: {}", ids);
        return vocabularyRepository.findAllById(ids).stream()
                .map(v ->new OnlyWordDto(v.getId(), v.getWord())).toList();
    }

    /**
     * Pobiera pełne dane słowa według ID.
     * 
     * <p>Metoda zwraca kompletny obiekt {@link WordDto} zawierający:
     * <ul>
     *   <li>ID i słowo</li>
     *   <li>Listę tłumaczeń</li>
     *   <li>Pełne obiekty zdań przykładowych (nie tylko ID)</li>
     *   <li>Pełne obiekty zdań wygenerowanych przez AI</li>
     * </ul>
     * 
     * @param id ID słowa do pobrania
     * @return Optional zawierający WordDto z pełnymi danymi słowa
     * @throws VocabularyNotFoundException gdy ID jest null lub puste
     * @see WordDto
     * @see SentenceDto
     */
    @Override
    public Optional<WordDto> getWordById(String id) {
        if (id == null || id.isBlank())
            throw new VocabularyNotFoundException(id);

        log.info("Pobieranie pełnego słowa o id: {}", id);

        return vocabularyRepository.findById(id).map(vocabulary -> {
            List<SentenceDto> sentences = sentenceRepository.findAllById(vocabulary.getSentenceIds())
                    .stream()
                    .map(s -> new SentenceDto(s.getId(), s.getSentence(), s.getTranslation()))
                    .toList();

            List<SentenceDto> sentencesAI = sentenceAIRepository.findAllById(vocabulary.getSentenceAIds())
                    .stream()
                    .map(s -> new SentenceDto(s.getId(), s.getSentenceAI(), s.getTranslationAI()))
                    .toList();

            return new WordDto(
                    vocabulary.getId(),
                    vocabulary.getWord(),
                    vocabulary.getTranslations(),
                    sentences,
                    sentencesAI
            );
        });
    }

    /**
     * Pobiera pełne dane wielu słów według listy ID.
     * 
     * <p>Metoda zwraca listę kompletnych obiektów {@link WordDto}, każdy zawierający:
     * <ul>
     *   <li>ID i słowo</li>
     *   <li>Listę tłumaczeń</li>
     *   <li>Pełne obiekty zdań przykładowych</li>
     *   <li>Pełne obiekty zdań wygenerowanych przez AI</li>
     * </ul>
     * 
     * @param ids Lista ID słów do pobrania
     * @return Lista WordDto z pełnymi danymi słów
     * @throws IllegalArgumentException gdy lista ids jest null lub pusta
     * @see WordDto
     * @see SentenceDto
     */
    @Override
    public List<WordDto> getWordsByIds(List<String> ids) {
        if (ids == null || ids.isEmpty())
            throw new IllegalArgumentException("ids must not be empty");

        log.info("Pobieranie {} pełnych słów", ids.size());

        return vocabularyRepository.findAllById(ids).stream()
                .map(vocabulary -> {
                    List<SentenceDto> sentences = sentenceRepository.findAllById(vocabulary.getSentenceIds())
                            .stream()
                            .map(s -> new SentenceDto(s.getId(), s.getSentence(), s.getTranslation()))
                            .toList();

                    List<SentenceDto> sentencesAI = sentenceAIRepository.findAllById(vocabulary.getSentenceAIds())
                            .stream()
                            .map(s -> new SentenceDto(s.getId(), s.getSentenceAI(), s.getTranslationAI()))
                            .toList();

                    return new WordDto(
                            vocabulary.getId(),
                            vocabulary.getWord(),
                            vocabulary.getTranslations(),
                            sentences,
                            sentencesAI
                    );
                })
                .toList();
    }
}

