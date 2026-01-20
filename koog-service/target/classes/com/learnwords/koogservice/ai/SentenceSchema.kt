package com.learnwords.koogservice.ai

import ai.koog.agents.core.tools.annotations.LLMDescription
import com.fasterxml.jackson.annotation.JsonFormat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import utils.InstantAsStringSerializer
import java.time.Instant

/**
 * Schemat wyniku generowania zdań.
 */
@Serializable
@SerialName("schema")
@LLMDescription(
    """
    Schemat wyniku generowania zdań.
    - schema: Wersja używanego schematu.
    - wordId: Unikalny identyfikator słowa, dla którego wygenerowano zdania.
    - target: Informacje o grupie docelowej dla generowanych zdań.
    - sentences: Lista wygenerowanych zdań wraz z ich tłumaczeniami.
    - metadata: Dodatkowe informacje o procesie generowania.
    """)
data class SentenceSchema(
    @property:LLMDescription("Wersja używanego schematu.")
    val schema: String = "ai.sentence.result.v1",
    @property:LLMDescription("Unikalny identyfikator słowa, dla którego wygenerowano zdania.")
    val wordId: String,
    @property:LLMDescription("Słowo, dla którego zostały wygenerowane zdania.")
    val word: String,
    @property:LLMDescription("Lista tłumaczeń danego słowa.")
    val translations: List<String>,
    @property:LLMDescription("Szczegóły dotyczące grupy docelowej generowanych zdań.")
    val target: Target,
    @property:LLMDescription("Lista wygenerowanych zdań wraz z ich tłumaczeniami.")
    val sentences: List<Sentence>,
    @property:LLMDescription("Dodatkowe informacje o procesie generowania.")
    val metadata: Metadata
)

/**
 * Dane docelowe dla generowanych zdań.
 */
@Serializable
@SerialName("target")
@LLMDescription(
    """
    Informacje o grupie docelowej dla generowanych zdań.
    - level: Poziom zaawansowania uczącego się (np. A1, B2, C1).
    - languageFrom: Język źródłowy, z którego generowane są zdania.
    - languageTo: Język docelowy, na który zdania są tłumaczone.
    - category: Kategoria tematyczna zdań (np. Podróże, Biznes, Codzienne rozmowy).
    """)
data class Target(
    @property:LLMDescription("Poziom zaawansowania uczącego się (np. A1, B2, C1).")
    val level: String,
    @property:LLMDescription("Język źródłowy, z którego generowane są zdania.")
    val languageFrom: String,
    @property:LLMDescription("Język docelowy, na który zdania są tłumaczone.")
    val languageTo: String,
    @property:LLMDescription("Kategoria tematyczna zdań (np. Podróże, Biznes, Codzienne rozmowy).")
    val category: String
)

/**
 * Wygenerowane zdanie z tłumaczeniem.
 */
@Serializable
@SerialName("sentence")
@LLMDescription(
    """
    Wygenerowane zdanie wraz z jego tłumaczeniem.
    - text: Zdanie wygenerowane w języku źródłowym.
    - translation: Tłumaczenie zdania na język docelowy.
    """)
data class Sentence(
    @property:LLMDescription("Zdanie wygenerowane w języku źródłowym.")
    val text: String,
    @property:LLMDescription("Tłumaczenie zdania na język docelowy.")
    val translation: String
)

/**
 * Metadane procesu generowania.
 */
@Serializable
data class Metadata(
    @property:LLMDescription("Wersja promptu użytego do generowania.")
    val promptVersion: Int,
    @property:LLMDescription("Model użyty do generowania zdań.")
    val model: String,
//    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Serializable(with = InstantAsStringSerializer::class)
    @property:LLMDescription("Znacznik czasu określający moment wygenerowania zdań.")
    val generatedAt: Instant
)
