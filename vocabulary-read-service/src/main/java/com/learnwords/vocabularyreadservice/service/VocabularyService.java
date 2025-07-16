package com.learnwords.vocabularyreadservice.service;

import com.learnwords.common.EventType;
import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.dto.VocabularyDto;
import com.learnwords.vocabularyreadservice.dto.OnlyWordDto;
import com.learnwords.vocabularyreadservice.dto.ResponseVocabularyDto;
import com.learnwords.vocabularyreadservice.entity.Vocabulary;
import com.learnwords.vocabularyreadservice.repository.VocabularyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class VocabularyService {
    private final VocabularyRepository vocabularyRepository;

    public VocabularyService(VocabularyRepository vocabularyRepository){
        this.vocabularyRepository = vocabularyRepository;
    }

    @Transactional
    @KafkaListener(topics = KafkaTopic.CREATE_VOCABULARY_TOPIC, groupId = KafkaGroup.VOCABULARY_READ_SERVICE_GROUP, containerFactory = "vocabularyKafkaListenerFactory")
    public void processSentenceCreate(VocabularyDto vocabularyDto) {
        log.info("Otrzymano event: {}", EventType.CREATE_VOCABULARY);
        Vocabulary vocabulary = new Vocabulary();
        try{
            vocabulary.setId(vocabularyDto.id());
            vocabulary.setWord(vocabularyDto.word());
            vocabulary.setTranslations(vocabularyDto.translations());
            vocabulary.setSentenceIds(vocabularyDto.sentenceIds());
            vocabularyRepository.save(vocabulary);
        } catch (DataAccessException e) {
            log.error("Błąd dostępu do bazy danych: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd dostępu do bazy danych: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Błąd podczas przetwarzania słowa: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd podczas przetwarzania słowa: " + e.getMessage(), e);
        }
    }
    public ResponseVocabularyDto getVocabulary(String id) {
        log.info("Pobieranie słowa o id: {}", id);
        Vocabulary vocabulary = vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono słowa o id: " + id));
        return new ResponseVocabularyDto(vocabulary.getWord(), vocabulary.getTranslations(), vocabulary.getSentenceIds());
    }

    public List<ResponseVocabularyDto> getVocabularies(List<String> ids) {
        log.info("Pobieranie słów o id: {}", ids);
        try {
            List<Vocabulary> vocabularyList = vocabularyRepository.findAllById(ids);
            return vocabularyList.stream()
                    .map(v -> new ResponseVocabularyDto(v.getWord(), v.getTranslations(), v.getSentenceIds()))
                    .toList();
        } catch (DataAccessException e) {
            log.error("Błąd dostępu do bazy danych: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd dostępu do bazy danych: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania słów: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd podczas pobierania słów: " + e.getMessage(), e);
        }
    }

    public List<OnlyWordDto> getOnlyWordsByIds(List<String> ids) {
        log.info("Pobieranie słów o id: {}", ids);
        try {
            List<Vocabulary> vocabularyList = vocabularyRepository.findAllById(ids);
            return vocabularyList.stream()
                    .map(v -> new OnlyWordDto(v.getId(), v.getWord()))
                    .toList();
        }
        catch (DataAccessException e) {
            log.error("Błąd dostępu do bazy danych: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd dostępu do bazy danych: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania słów: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd podczas pobierania słów: " + e.getMessage(), e);
        }

    }
}
