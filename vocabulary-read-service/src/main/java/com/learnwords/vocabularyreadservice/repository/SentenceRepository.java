package com.learnwords.vocabularyreadservice.repository;



import com.learnwords.vocabularyreadservice.entity.Sentence;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SentenceRepository extends MongoRepository<Sentence,String> {
}
