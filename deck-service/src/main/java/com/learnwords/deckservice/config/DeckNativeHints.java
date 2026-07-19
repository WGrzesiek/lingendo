package com.learnwords.deckservice.config;

import com.learnwords.deckservice.dto.facade.review.ReviewCounters;
import com.learnwords.deckservice.dto.session.FlashcardSessionNumber;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * Native reflection hints for JPQL {@code select new ...} constructor-expression DTOs.
 * Hibernate resolves these constructors reflectively at query-compile time; without
 * registration the native image throws SemanticException: Missing constructor.
 */
public class DeckNativeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        for (Class<?> type : new Class<?>[]{FlashcardSessionNumber.class, ReviewCounters.class}) {
            hints.reflection().registerType(type,
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                    MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                    MemberCategory.DECLARED_FIELDS,
                    MemberCategory.INVOKE_DECLARED_METHODS);
        }
    }
}
