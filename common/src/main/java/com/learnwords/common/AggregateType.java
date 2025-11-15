package com.learnwords.common;

/**
 * Typy agregatów używane w systemie do kategoryzacji różnych jednostek domenowych.
 * Connector debezium dodaje prefiks "outbox.event." do nazw typów agregatów przy publikacji eventów.
 *
 * <p>Definiuje różne typy agregatów, takie jak:
 * <ul>
 *   <li>VOCABULARY - Pojedyncze słowo wraz z tłumaczeniami i przykładowymi zdaniami.</li>
 *   <li>VOCABULARYFORDECK - Słowo przypisane do konkretnej talii (decka) w systemie nauki słówek.</li>
 *   <li>SENTENCEFORDECK - Przykładowe zdanie przypisane do konkretnej talii (decka).</li>
 *   <li>SENTENCE - Pojedyncze przykładowe zdanie używane do ilustrowania użycia słowa.</li>
 * </ul>
 *
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2024-06-01
 */

public enum AggregateType {
    VOCABULARY,
    VOCABULARYFORDECK,
    SENTENCEFORDECK,
    SENTENCE;
}
