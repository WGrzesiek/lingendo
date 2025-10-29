package com.learnwords.vocabularyreadservice.repository;

import com.learnwords.vocabularyreadservice.entity.Vocabulary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VocabularyRepository extends MongoRepository<Vocabulary, String> {
}