package com.learnwords.vocabularyreadservice.repository;

import com.learnwords.vocabularyreadservice.entity.Vocabulary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabularyRepository extends MongoRepository<Vocabulary, String> {
    
    @Query("{ '_id': ?0 }")
    @Update("{ '$push': { 'sentenceAIds': { '$each': ?1 } } }")
    void addSentenceAIIds(String wordId, List<String> sentenceAIIds);
}