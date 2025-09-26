package com.learnwords.vocabularyreadservice.repository;

import com.learnwords.vocabularyreadservice.entity.Sentence;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface SentenceRepository extends ReactiveMongoRepository<Sentence,String> {

    @Aggregation(pipeline = {
            "{ $sample: { size: ?0 } }"
    })
    Mono<List<Sentence>> findRandom(int size);

    @Aggregation(pipeline = {
            "{ $sample: { size: ?0 } }",
            "{ $sort: { sentence: 1 } }"
    })
    Mono<List<Sentence>> findRandomSortedAlphabetically(int size);

}
