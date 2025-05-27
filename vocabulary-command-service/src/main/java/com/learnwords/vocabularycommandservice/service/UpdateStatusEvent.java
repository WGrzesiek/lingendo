package com.learnwords.vocabularycommandservice.service;

import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.dto.UpdateStatusDto;
import com.learnwords.vocabularycommandservice.repository.OutboxRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class UpdateStatusEvent {
    private final OutboxRepository outboxRepository;

    public UpdateStatusEvent(OutboxRepository outboxRepository){
        this.outboxRepository = outboxRepository;
    }

//    @KafkaListener(topics = KafkaTopic.UPPATED_STATUS, groupId = KafkaGroup.VOCABULARY_COMMAND_GROUP)
//    @Transactional
//    public void confirmCreateSentence(UpdateStatusDto updateStatusDto){
//        log.info("slucham update");
//        outboxRepository.findById(updateStatusDto.id()).ifPresent(outbox -> {
//            outbox.setEventStatus(updateStatusDto.eventStatus());
//            outbox.setUpdatedAt(new Date());
//            outboxRepository.save(outbox);
//        });
//    }
}
