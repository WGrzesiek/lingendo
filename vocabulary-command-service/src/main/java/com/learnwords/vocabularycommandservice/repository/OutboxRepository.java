package com.learnwords.vocabularycommandservice.repository;

import com.learnwords.vocabularycommandservice.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<Outbox, String> {
}