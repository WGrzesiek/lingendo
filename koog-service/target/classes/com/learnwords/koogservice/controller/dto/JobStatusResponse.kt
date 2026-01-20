package com.learnwords.koogservice.controller.dto

import java.time.Instant


data class JobStatusResponse(
    val jobId: String?,
    val correlationId: String,
    val status: String,
    val itemsTotal: Int,
    val itemsSucceeded: Int,
    val itemsFailed: Int,
    val itemsPending: Int,
    val itemsProcessing: Int,
    val requestedAt: Instant,
    val startedAt: Instant?,
    val finishedAt: Instant?
)
