package com.learnwords.vocabularycommandservice.repository;

import com.learnwords.common.EventStatus;
import com.learnwords.vocabularycommandservice.entity.Outbox;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OutboxRepository extends JpaRepository<Outbox, String> {

    @Modifying
    @Transactional
    @Query(value = """
  UPDATE outbox
     SET event_status = :eventStatus
   WHERE aggregate_id     = :aggregate_id
""", nativeQuery = true)
    void updateOutboxEventStatus(@Param("aggregateId") String aggregate_id, @Param("eventStatus") String eventStatus);
}