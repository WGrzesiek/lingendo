package com.learnwords.koogservice.controller

import com.learnwords.koogservice.controller.dto.GenerateSentencesResponse
import com.learnwords.koogservice.controller.dto.JobStatusResponse
import com.learnwords.koogservice.enums.EventStatus
import com.learnwords.koogservice.messaging.dto.SentenceGenerationRequestDto
import com.learnwords.koogservice.messaging.dto.WordItemDto
import com.learnwords.koogservice.persistence.repository.SentenceGenerationItemRepository
import com.learnwords.koogservice.persistence.repository.SentenceGenerationJobRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * Kontroler REST API do generowania zdań AI.
 */
@RestController
@RequestMapping("/api/v1/sentences")
@Tag(name = "Generowanie zdań AI", description = "Endpointy do generowania przykładowych zdań przez AI")
class SentenceGeneratorController(
    private val jobRepository: SentenceGenerationJobRepository,
    private val itemRepository: SentenceGenerationItemRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    
    private val log = LoggerFactory.getLogger(SentenceGeneratorController::class.java)
    
    companion object {
        const val SENTENCE_REQUEST_TOPIC = "ai.sentence.request"
    }
    
    /**
     * Wysyła żądanie generowania zdań.
     */
    @PostMapping("/generate")
    @Operation(
        summary = "Generuj zdania przykładowe",
        description = """
            Wysyła żądanie generowania przykładowych zdań dla podanych słówek.
            
            Przetwarzanie jest asynchroniczne - endpoint zwraca jobId,
            który można użyć do sprawdzenia statusu.
        """
    )
    @ApiResponses(
        ApiResponse(responseCode = "202", description = "Żądanie przyjęte do przetwarzania"),
        ApiResponse(responseCode = "400", description = "Nieprawidłowe dane wejściowe"),
        ApiResponse(responseCode = "500", description = "Błąd serwera")
    )
    fun generateSentences(
        @RequestBody request: SentenceGenerationRequestDto
    ): ResponseEntity<GenerateSentencesResponse> {
        log.info(
            "Otrzymano żądanie generowania zdań - userId: {}, liczba słówek: {}",
            request.requestedByUserId,
            request.words.size
        )
        
        if (request.words.isEmpty()) {
            log.warn("Żądanie bez słówek - userId: {}", request.requestedByUserId)
            return ResponseEntity.badRequest().build()
        }
        
        val correlationId = UUID.randomUUID().toString()
        
        // Wysyłamy żądanie przez Kafka
        val kafkaRequest = SentenceGenerationRequestDto(
            id = correlationId,
            requestedByUserId = request.requestedByUserId,
            words = request.words.map { word ->
                WordItemDto(
                    wordId = word.wordId,
                    word = word.word,
                    translations = word.translations
                )
            },
            level = request.level,
            category = request.category,
            languageFrom = request.languageFrom,
            languageTo = request.languageTo,
        )
        
        kafkaTemplate.send(SENTENCE_REQUEST_TOPIC, correlationId, kafkaRequest)
        
        log.info("Wysłano żądanie do Kafka - correlationId: {}", correlationId)
        
        return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .body(
                GenerateSentencesResponse(
                    correlationId = correlationId,
                    message = "Żądanie przyjęte do przetwarzania",
                    wordsCount = request.words.size
                )
            )
    }
    
    /**
     * Pobiera status zadania generowania.
     */
    @GetMapping("/jobs/{jobId}")
    @Operation(
        summary = "Pobierz status zadania",
        description = "Zwraca aktualny status zadania generowania zdań."
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Status zadania"),
        ApiResponse(responseCode = "404", description = "Zadanie nie istnieje")
    )
    fun getJobStatus(
        @Parameter(description = "ID zadania")
        @PathVariable jobId: String?
    ): ResponseEntity<JobStatusResponse> {
        log.debug("Pobieranie statusu zadania - jobId: {}", jobId)
        
        val job = jobRepository.findByJobId(jobId)
        
        val successCount = itemRepository.countByJobJobIdAndStatus(jobId, EventStatus.SUCCESS)
        val failCount = itemRepository.countByJobJobIdAndStatus(jobId, EventStatus.FAILED)
        val pendingCount = itemRepository.countByJobJobIdAndStatus(jobId, EventStatus.PENDING)
        val processingCount = itemRepository.countByJobJobIdAndStatus(jobId, EventStatus.PROCESSING)
        
        return ResponseEntity.ok(
            JobStatusResponse(
                jobId = job.jobId,
                correlationId = job.correlationId,
                status = job.status.name,
                itemsTotal = job.itemsTotal,
                itemsSucceeded = successCount.toInt(),
                itemsFailed = failCount.toInt(),
                itemsPending = pendingCount.toInt(),
                itemsProcessing = processingCount.toInt(),
                requestedAt = job.requestedAt,
                startedAt = job.startedAt,
                finishedAt = job.finishedAt
            )
        )
    }
    
    /**
     * Pobiera status zadania po correlationId.
     */
    @GetMapping("/jobs/correlation/{correlationId}")
    @Operation(
        summary = "Pobierz status zadania po correlationId",
        description = "Zwraca aktualny status zadania na podstawie correlationId."
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Status zadania"),
        ApiResponse(responseCode = "404", description = "Zadanie nie istnieje")
    )
    fun getJobStatusByCorrelationId(
        @Parameter(description = "ID korelacji")
        @PathVariable correlationId: String
    ): ResponseEntity<JobStatusResponse> {
        log.debug("Pobieranie statusu zadania - correlationId: {}", correlationId)
        val job = jobRepository.findByCorrelationId(correlationId)
            ?: return ResponseEntity.notFound().build()
        return getJobStatus(job.jobId)
    }
}
