package com.learnwords.vocabularycommandservice.service;

import com.learnwords.common.dto.UpdateOutboxEventDto;

public interface UpdateOutboxEvent {
    void updateOutboxEvent(UpdateOutboxEventDto updateOutboxEventDto);
}
