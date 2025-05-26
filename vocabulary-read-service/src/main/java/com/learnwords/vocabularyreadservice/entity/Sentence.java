package com.learnwords.vocabularyreadservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document(collection = "sentence")
public class Sentence {
    @Id
    private String id;
    private String sentence;
    private String translation;
    private Date createdAt;
    private Date updatedAt;

}