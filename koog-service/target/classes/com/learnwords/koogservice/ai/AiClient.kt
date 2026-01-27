package com.learnwords.koogservice.ai

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.functionalStrategy
import ai.koog.agents.core.dsl.extension.asAssistantMessage
import ai.koog.agents.core.dsl.extension.requestLLM
import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.structure.StructureFixingParser
import ai.koog.prompt.structure.executeStructured
import com.learnwords.koogservice.application.dto.SentenceStructuredResult
import com.learnwords.koogservice.config.AI_CONFIG
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Objects

@Component
class AiClient(
    @Value("\${openai.api-key}")
    private val apiKey: String,

    @Value("\${ai.sentence.temperature:0.7}")
    private val temperature: Double,

    @Value("\${ai.sentence.model:gpt-4o-mini}")
    private var aiModelType: String
) {

    private val promptExecutor by lazy { simpleOpenAIExecutor(apiKey) }

    /**
     * Agent do prostych odpowiedzi tekstowych (Basic Agent)
     */
    private val agent: AIAgent<String, String> by lazy {
        AIAgent(
            promptExecutor = promptExecutor,
            systemPrompt = AI_CONFIG,
            llmModel = when (aiModelType.lowercase()) {
                "gpt-4o-mini" -> OpenAIModels.Chat.GPT4oMini
                "gpt-5-mini" -> OpenAIModels.Chat.GPT5Mini
                "gpt-5-nano" -> OpenAIModels.Chat.GPT5Nano
                else -> OpenAIModels.Chat.GPT4oMini
            },
            strategy = functionalStrategy { input ->
                val response = requestLLM(input)
                response.asAssistantMessage().content
            },
            temperature = temperature
        )
    }

    /**
     * Proste generowanie TEKSTU (Agent).
     */
    suspend fun generateText(input: String): String {
        return agent.run(input)
    }

    /**
     * Generowanie zdań ze STRUKTURĄ (Schema).
     */
    suspend fun generateSentenceStructured(
        userPrompt: String,
    ): SentenceStructuredResult{
        val result = promptExecutor.executeStructured<SentenceSchema>(
//            prompt = Prompt(userPrompt),
            prompt = prompt("Generate Sentences") {
                system(AI_CONFIG)
                user(
                    userPrompt
                )
            },
            model = OpenAIModels.Chat.GPT4oMini,
            examples = examples,
            fixingParser = StructureFixingParser(
                model = OpenAIModels.Chat.GPT4o, // „naprawiacz”
                retries = 1
            )
        )

        val structured = result.getOrElse { throw it }
        // gpt-5-mini	$0.25	$0.025	$2.00

        val inputTokens = structured.message.metaInfo.inputTokensCount ?: 0
        val outputTokens = structured.message.metaInfo.outputTokensCount ?: 0

        val inputCost = BigDecimal.valueOf(inputTokens.toLong())
            .multiply(BigDecimal("0.25"))
            .divide(BigDecimal("1000000"), 8, RoundingMode.HALF_UP)

        val outputCost = BigDecimal.valueOf(outputTokens.toLong())
            .multiply(BigDecimal("2.00"))
            .divide(BigDecimal("1000000"), 8, RoundingMode.HALF_UP)

        val totalCost = inputCost.add(outputCost)
        return SentenceStructuredResult(
            json = structured.data,
            metadata = structured.message.metaInfo.metadata,
            inputTokensCount = inputTokens,
            outputTokensCount = outputTokens,
            totalTokensCount = structured.message.metaInfo.totalTokensCount,
            cost = totalCost
        )
    }
}
