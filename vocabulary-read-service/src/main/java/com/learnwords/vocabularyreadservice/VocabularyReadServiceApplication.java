package com.learnwords.vocabularyreadservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class VocabularyReadServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VocabularyReadServiceApplication.class, args);
    }

}
