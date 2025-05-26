package com.learnwords.vocabularyreadservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "vocabulary")
public class Vocabulary {
    @Id
    private String id;
    private String word;
    private List<String> translations;
    private List<String> sentenceIds;
    private Date createdAt;
    private Date updatedAt;

}