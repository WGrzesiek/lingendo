package com.learnwords.koogservice.ai

import java.time.Instant

val examples: List<SentenceSchema> = listOf(
    SentenceSchema(
        schema = "ai.sentence.result.v1",
        wordId = "word-123",
        word = "cat",
        translations = listOf("kot", "kotek"),
        target = Target(
            level = "A1",
            languageFrom = "ENGLISH",
            languageTo = "POLISH",
            category = "HOME"
        ),
        sentences = listOf(
            Sentence(
                text = "I have a cat.",
                translation = "Mam kota."
            ),
            Sentence(
                text = "The cat is on the sofa.",
                translation = "Kot jest na kanapie."
            )
        ),
        metadata = Metadata(
            promptVersion = 1,
            model = "gpt-4o-mini",
            generatedAt = Instant.parse("2026-01-20T10:00:00Z")
        )
    ),

    SentenceSchema(
        schema = "ai.sentence.result.v1",
        wordId = "word-987",
        word = "negotiate",
        translations = listOf("negocjować", "pertraktować"),
        target = Target(
            level = "B2",
            languageFrom = "ENGLISH",
            languageTo = "POLISH",
            category = "BUSINESS"
        ),
        sentences = listOf(
            Sentence(
                text = "We need to negotiate the terms before signing the contract.",
                translation = "Musimy wynegocjować warunki przed podpisaniem umowy."
            ),
            Sentence(
                text = "She negotiated a better price by offering a long-term partnership.",
                translation = "Wynegocjowała lepszą cenę, oferując długoterminową współpracę."
            )
        ),
        metadata = Metadata(
            promptVersion = 1,
            model = "gpt-4o-mini",
            generatedAt = Instant.parse("2026-01-20T10:00:00Z")
        )
    )
)