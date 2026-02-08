package com.learnwords.koogservice.controller.dto

data class GenerateSentencesResponse(
    val correlationId: String,
    val message: String,
    val wordsCount: Int

)
