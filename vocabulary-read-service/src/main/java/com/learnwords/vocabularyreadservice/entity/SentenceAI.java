package com.learnwords.vocabularyreadservice.entity;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document(collection = "sentenceAI")
public class SentenceAI {
    @Id
    private String id;
    private String sentenceAI;
    private String translationAI;
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
