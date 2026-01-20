package com.learnwords.koogservice.application

import com.learnwords.koogservice.messaging.dto.SentenceGenerationRequestDto

/**
 * Interfejs serwisu generowania zdań.
 */
interface SentenceGenerationService {
    fun processGenerationRequest(request: SentenceGenerationRequestDto)
//    fun handleGenerationFailure(correlationId: String, error: Exception)
}
