package com.learnwords.vocabularyreadservice.entity;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document(collection = "vocabulary")
public class Vocabulary {
    @Id
    private String id;
    private String word;
    private List<String> translations;
    private List<String> sentenceIds;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

}