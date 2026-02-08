package com.learnwords.vocabularyreadservice.repository;

import com.learnwords.vocabularyreadservice.entity.Sentence;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SentenceRepository extends MongoRepository<Sentence,String> {

    @Aggregation(pipeline = {
            "{ $sample: { size: ?0 } }"
    })
    List<Sentence> findRandom(int size);

    @Aggregation(pipeline = {
            "{ $sample: { size: ?0 } }",
            "{ $sort: { sentence: 1 } }"
    })
    List<Sentence> findRandomSortedAlphabetically(int size);

}
