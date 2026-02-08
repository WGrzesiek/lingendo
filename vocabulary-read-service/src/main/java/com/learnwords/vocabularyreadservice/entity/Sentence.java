package com.learnwords.vocabularyreadservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

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