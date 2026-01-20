package com.learnwords.koogservice.ai

/**
 * Builder promptów dla generowania zdań.
 * 
 * Zawiera metody pomocnicze do budowania promptów
 * dla różnych poziomów językowych i kategorii.
 */
object SentencePrompt {
    
    /**
     * Buduje prompt dla generowania zdań.
     */
    fun build(
        word: String,
        translations: List<String>,
        languageFrom: String,
        languageTo: String,
        level: String,
        category: String,
        sentencesCount: Int = 3
    ): String {
        val translationsStr = translations.joinToString(", ")
        
        return """
            Wygeneruj $sentencesCount przykładowych zdań dla słówka.
            
            Słówko: $word
            Tłumaczenia: $translationsStr
            Język źródłowy: $languageFrom
            Język docelowy: $languageTo
            Poziom językowy: $level
            Kategoria: $category
            
            Wymagania:
            1. Każde zdanie musi zawierać słówko "$word"
            2. Zdania powinny być na poziomie $level (${getLevelDescription(level)})
            3. Zdania powinny być tematycznie związane z kategorią "$category"
            4. Dla każdego zdania podaj tłumaczenie na język $languageTo
            5. Zdania powinny być naturalne i użyteczne w codziennej komunikacji
            Zwróć tylko JSON, bez dodatkowych komentarzy.
        """.trimIndent()
    }

    /**
     * Zwraca opis poziomu językowego.
     */
    private fun getLevelDescription(level: String): String = when (level.uppercase()) {
        "A1" -> "początkujący - proste słowa i frazy"
        "A2" -> "podstawowy - proste zdania o codziennych sytuacjach"
        "B1" -> "średnio-zaawansowany - klarowne, standardowe wypowiedzi"
        "B2" -> "zaawansowany - złożone teksty na różne tematy"
        "C1" -> "biegły - elastyczne i skuteczne użycie języka"
        "C2" -> "mistrz - swobodne rozumienie i wyrażanie się"
        else -> "średnio-zaawansowany"
    }
}