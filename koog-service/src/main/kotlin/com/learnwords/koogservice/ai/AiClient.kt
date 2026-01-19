package com.learnwords.koogservice.ai

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.functionalStrategy
import ai.koog.agents.core.dsl.extension.asAssistantMessage
import ai.koog.agents.core.dsl.extension.requestLLM
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import com.learnwords.koogservice.config.AI_CONFIG

class AiClient {
    private val apiKey: String =
        System.getenv("OPENAI_API_KEY")
            ?: error("OPENAI_API_KEY not set")

    val agent = AIAgent<String, String>(
        promptExecutor = simpleOpenAIExecutor(apiKey),
        systemPrompt = AI_CONFIG,
        llmModel = OpenAIModels.Chat.GPT5Mini,
        strategy = functionalStrategy { input ->
            val response = requestLLM(input)
            response.asAssistantMessage().content
        },
        temperature = 0.7,
    )
}
