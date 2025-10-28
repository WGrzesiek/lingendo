package com.learnwords.vocabularyreadservice.service;

import com.learnwords.common.dto.UpdateOutboxEventDto;

public interface UpdateOutboxEvent {

    void processUpdateOutboxEvent(UpdateOutboxEventDto updateOutboxEventDto);
}
