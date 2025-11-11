package com.learnwords.vocabularyreadservice.repository;

import com.learnwords.vocabularyreadservice.entity.SentenceAI;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SentenceAIRepository extends MongoRepository<SentenceAI,String> {
}
