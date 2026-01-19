package com.learnwords.koogservice.ai

import ai.koog.agents.core.tools.annotations.LLMDescription
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
@SerialName("schema")
@LLMDescription(
    """
    Schema for the sentence generation result.
    - schema: The version of the schema being used.
    - wordId: The unique identifier for the word associated with the sentences.
    - target: Details about the target audience for the sentences.
    - sentences: A list of generated sentences with their translations.
    - metadata: Additional information about the generation process.
    """)
data class SentenceSchema(
    val schema: String = "ai.sentence.result.v1",
    val wordId: String,
    val target: Target,
    val sentences: List<Sentence>,
    val metadata: Metadata

)

@Serializable
@SerialName("target")
@LLMDescription(
    """
    Details about the target audience for the sentences.
    - level: The proficiency level of the learner (e.g., A1, B2, C1).
    - languageFrom: The source language from which the sentences are translated.
    - languageTo: The target language into which the sentences are translated.
    - category: The thematic category of the sentences (e.g., Travel, Business, Everyday Conversations).
    """)
data class Target(
    val level: String,
    val languageFrom: String,
    val languageTo: String,
    val category: String
){

}

@Serializable
@SerialName("sentence")
@LLMDescription(
    """
    A generated sentence and its translation.
    - text: The generated sentence in the source language.
    - translation: The translation of the sentence into the target language.
    """)
data class Sentence(
    val text: String,
    val translation: String,
)

@Serializable
data class Metadata(
    val promptVersion: Int,
    val model: String,
    val generatedAt: Instant
)