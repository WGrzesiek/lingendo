package com.learnwords.koogservice.application.dto

import com.learnwords.koogservice.ai.SentenceSchema
import java.math.BigDecimal

data class SentenceStructuredResult(
    val json: SentenceSchema,
    val metadata: Any?,
    val inputTokensCount: Int?,
    val outputTokensCount: Int?,
    val totalTokensCount: Int?,
    val cost: BigDecimal?,
)