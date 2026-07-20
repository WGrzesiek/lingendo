package com.learnwords.common.config;

import org.springframework.aot.hint.BindingReflectionHintsRegistrar;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;

/**
 * Native reflection hints for domain event classes serialized over Kafka.
 *
 * <p>Producers use Spring Kafka's JsonSerializer (Jackson) to write events like
 * {@code DeckCreatedEvent}; consumers deserialize them. Under GraalVM native image
 * Jackson cannot reach a record's components/accessors (and nested enums) without
 * reflection metadata, failing with
 * {@code InvalidDefinitionException: No serializer found for class ...}.
 *
 * <p>Registered globally via {@code META-INF/spring/aot.factories}; every service that
 * depends on {@code common} gets the whole {@code com.learnwords.common.events} graph.
 */
public class EventsNativeHints implements RuntimeHintsRegistrar {

    private static final String EVENTS_PATTERN = "classpath*:com/learnwords/common/events/**/*.class";

    private final BindingReflectionHintsRegistrar binding = new BindingReflectionHintsRegistrar();

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        ClassLoader loader = classLoader != null ? classLoader : getClass().getClassLoader();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(loader);
        CachingMetadataReaderFactory readers = new CachingMetadataReaderFactory(loader);
        try {
            Resource[] resources = resolver.getResources(EVENTS_PATTERN);
            for (Resource res : resources) {
                try {
                    String className = readers.getMetadataReader(res).getClassMetadata().getClassName();
                    Class<?> type = Class.forName(className, false, loader);
                    // Walks the whole graph (record components + nested enums) for Jackson binding.
                    binding.registerReflectionHints(hints.reflection(), type);
                } catch (Throwable ignored) {
                    // Skip entries that cannot be read/resolved.
                }
            }
        } catch (Exception ignored) {
            // No event classes on the classpath — nothing to register.
        }
    }
}
