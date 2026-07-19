package com.learnwords.common.config;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * Native reflection hints for Spring Data pagination types.
 *
 * <p>Controllers that return {@code Page}/{@code Slice} serialize {@code PageImpl} and the
 * embedded {@code Sort}/{@code Sort.Order}. Under GraalVM native image Jackson cannot reach
 * their getters without reflection metadata, failing with
 * {@code HttpMessageNotWritableException: Couldn't serialize object <property>: <direction>}.
 *
 * <p>Registered globally via {@code META-INF/spring/aot.factories} so every service that
 * depends on {@code common} gets it without per-service wiring.
 */
public class PaginationNativeHints implements RuntimeHintsRegistrar {

    private static final MemberCategory[] CATEGORIES = {
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
            MemberCategory.INVOKE_PUBLIC_METHODS,
            MemberCategory.INVOKE_DECLARED_METHODS,
            MemberCategory.DECLARED_FIELDS
    };

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        ClassLoader loader = classLoader != null ? classLoader : getClass().getClassLoader();
        for (String fqn : new String[]{
                "org.springframework.data.domain.PageImpl",
                "org.springframework.data.domain.Chunk",
                "org.springframework.data.domain.SliceImpl",
                "org.springframework.data.domain.Sort",
                "org.springframework.data.domain.Sort$Order",
                "org.springframework.data.domain.Sort$Direction",
                "org.springframework.data.domain.Sort$NullHandling",
                "org.springframework.data.domain.PageRequest",
                "org.springframework.data.domain.AbstractPageRequest",
                "org.springframework.data.domain.Unpaged",
                "org.springframework.data.domain.Pageable"
        }) {
            hints.reflection().registerTypeIfPresent(loader, fqn, CATEGORIES);
        }
    }
}
