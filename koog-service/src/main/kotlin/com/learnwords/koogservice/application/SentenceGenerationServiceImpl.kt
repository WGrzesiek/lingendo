package com.learnwords.koogservice.application

import com.learnwords.koogservice.ai.AiClient
import com.learnwords.koogservice.ai.SentencePrompt
import com.learnwords.koogservice.enums.EventStatus
import com.learnwords.koogservice.messaging.dto.*
import com.learnwords.koogservice.persistence.entity.Outbox
import com.learnwords.koogservice.persistence.entity.SentenceGenerationItem
import com.learnwords.koogservice.persistence.entity.SentenceGenerationJob
import com.learnwords.koogservice.persistence.repository.OutboxRepository
import com.learnwords.koogservice.persistence.repository.SentenceGenerationItemRepository
import com.learnwords.koogservice.persistence.repository.SentenceGenerationJobRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*
import kotlinx.serialization.json.Json
/**
 * Implementacja serwisu generowania zdań.
 *
 * Odpowiada za:
 * - Tworzenie zadań generowania (Job)
 * - Generowanie zdań przez AI (Koog Agent)
 * - Publikowanie wyników przez Kafka
 */
@Service
class SentenceGenerationServiceImpl(
    private val jobRepository: SentenceGenerationJobRepository,
    private val itemRepository: SentenceGenerationItemRepository,
    private val aiClient: AiClient,
    private val outboxRepository: OutboxRepository
) : SentenceGenerationService {

    private val log = LoggerFactory.getLogger(SentenceGenerationServiceImpl::class.java)

    @Value("\${ai.sentence.model:gpt-4o-mini}")
    private lateinit var aiModel: String

    @Value("\${ai.sentence.prompt-version:1}")
    private var promptVersion: Int = 1

    companion object {
        const val SCHEMA_VERSION = 1
        const val MAX_SENTENCES_PER_WORD = 3
        // Guard the OpenAI call so a stuck HTTP request surfaces as a per-item error instead of
        // hanging the Kafka listener forever (offset never commits, consumer blocked).
        const val AI_CALL_TIMEOUT_MS = 60_000L
    }

    /**
     * Przetwarza żądanie generowania zdań.
     */
    @Transactional
    override fun processGenerationRequest(request: SentenceGenerationRequestDto) {
        log.info("Rozpoczynam przetwarzanie żądania - correlationId: {}", request.id)

        val job = createJob(request)
        log.debug("Utworzono Job - jobId: {}", job.jobId)

        val items = createItems(job, request)
        log.debug("Utworzono {} items dla job: {}", items.size, job.jobId)

        jobRepository.updateJobStarted(job.jobId, EventStatus.PROCESSING, Instant.now())


        var successCount = 0
        var failCount = 0

        for (item in items) {
            try {
                processItem(item, request)
                successCount++
            } catch (e: Exception) {
                log.error("Błąd przetwarzania item {} - {}", item.itemId, e.message)
                failCount++
            }
        }

        val finalStatus = when {
            failCount == 0 -> EventStatus.SUCCESS
            successCount == 0 -> EventStatus.FAILED
            else -> EventStatus.PARTIAL_SUCCESS
        }

        jobRepository.updateJobFinished(
            job.jobId,
            finalStatus,
            successCount,
            failCount,
            Instant.now()
        )




        log.info(
            "Zakończono Job {} - status: {}, sukces: {}, błędy: {}",
            job.jobId, finalStatus, successCount, failCount
        )
    }

    /**
     * Tworzy nowy Job generowania.
     */
    private fun createJob(request: SentenceGenerationRequestDto): SentenceGenerationJob {
        val job = SentenceGenerationJob(
            jobId = UUID.randomUUID().toString(),
            correlationId = request.id,
            itemsTotal = request.words.size,
            requestedByUserId = request.requestedByUserId,
            status = EventStatus.PENDING,
            schemaVersion = SCHEMA_VERSION,
            requestedAt = Instant.now()
        )
        return jobRepository.save(job)
    }

    /**
     * Tworzy Items dla każdego słówka.
     */
    private fun createItems(
        job: SentenceGenerationJob,
        request: SentenceGenerationRequestDto
    ): List<SentenceGenerationItem> {
        return request.words.map { word ->
            val item = SentenceGenerationItem(
                itemId = UUID.randomUUID().toString(),
                job = job,
                wordId = word.wordId,
                languageFrom = request.languageFrom,
                languageTo = request.languageTo,
                level = request.level,
                category = request.category,
                promptVersion = promptVersion,
                model = aiModel,
                status = EventStatus.PENDING,
                attempts = 0,
                resultJson = null,
                errorCode = null,
                errorMessage = null,
                costEstimate = null
            )
            itemRepository.save(item)
        }
    }

    /**
     * Przetwarza pojedyncze słówko - generuje zdania przez AI.
     */
    private fun processItem(item: SentenceGenerationItem, request: SentenceGenerationRequestDto) {
        val word = request.words.find { it.wordId == item.wordId }
            ?: throw IllegalStateException("Nie znaleziono słówka ${item.wordId}")

        log.debug("Generuję zdania dla słówka: {} ({})", word.word, word.wordId)

        val prompt = SentencePrompt.build(
            word = word.word,
            translations = word.translations,
            languageFrom = request.languageFrom,
            languageTo = request.languageTo,
            level = request.level,
            category = request.category,
            sentencesCount = MAX_SENTENCES_PER_WORD
        )


        val response = runBlocking { withTimeout(AI_CALL_TIMEOUT_MS) { aiClient.generateSentenceStructured(prompt) } }
        
        // Nadpisujemy wordId wartością z requestu - AI czasami zwraca błędny identyfikator
        val correctedResult = response.json.copy(wordId = word.wordId)
        val resultJsonString: String = json.encodeToString(correctedResult)

        itemRepository.updateItemResult(item.itemId , EventStatus.SUCCESS, resultJsonString,
            response.inputTokensCount,
            response.outputTokensCount,
            response.totalTokensCount,
            response.cost
        )

        outboxRepository.save(Outbox(
            eventId = UUID.randomUUID().toString(),
            agregateType = "SentenceGeneration",
            agregateId = item.itemId ?: UUID.randomUUID().toString(),
            eventType = "SentenceGenerationCompleted",
            payload = resultJsonString,
            status = "PENDING",
            retryCount = 0,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        )
        log.debug("Wygenerowano {} zdań dla słówka: {}", response.json.sentences.size, word.word)
    }
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }

}
