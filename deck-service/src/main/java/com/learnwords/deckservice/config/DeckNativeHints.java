package com.learnwords.deckservice.config;

import com.learnwords.deckservice.dto.facade.review.ReviewCounters;
import com.learnwords.deckservice.dto.session.FlashcardSessionNumber;
import org.springframework.aot.hint.BindingReflectionHintsRegistrar;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;

/**
 * Native reflection hints for deck-service.
 * <ul>
 *   <li>JPQL {@code select new ...} constructor-expression DTOs (Hibernate resolves the
 *       constructor reflectively at query-compile time).</li>
 *   <li>net.devh {@code @GrpcClient} stub injection: the BeanPostProcessor sets the stub
 *       field reflectively and builds it via {@code <Service>Grpc.newBlockingStub(channel)};
 *       without hints the fields stay null and calls NPE at runtime.</li>
 *   <li>Polymorphic Jackson types in {@code service.evaluationService} (sealed UserAnswer with
 *       {@code @JsonSubTypes}, and the algorithm result hierarchy) — deserialization needs the
 *       whole graph registered for binding.</li>
 * </ul>
 */
public class DeckNativeHints implements RuntimeHintsRegistrar {

    private final BindingReflectionHintsRegistrar binding = new BindingReflectionHintsRegistrar();

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        ClassLoader loader = classLoader != null ? classLoader : getClass().getClassLoader();

        // JPQL 'select new ...' constructor-expression DTOs.
        for (Class<?> type : new Class<?>[]{FlashcardSessionNumber.class, ReviewCounters.class}) {
            hints.reflection().registerType(type,
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                    MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                    MemberCategory.DECLARED_FIELDS,
                    MemberCategory.INVOKE_DECLARED_METHODS);
        }

        // net.devh @GrpcClient injects stubs into these client fields reflectively.
        for (String fqn : new String[]{
                "com.learnwords.deckservice.service.grpcClient.impl.UserGrcpClientImpl",
                "com.learnwords.deckservice.service.grpcClient.impl.VocabularyGrpcClientImpl"
        }) {
            hints.reflection().registerTypeIfPresent(loader, fqn,
                    MemberCategory.DECLARED_FIELDS,
                    MemberCategory.INVOKE_DECLARED_METHODS);
        }

        // net.devh StubFactory reflectively calls <Service>Grpc.newBlockingStub(Channel).
        for (String fqn : new String[]{
                "com.learnwords.auth.v1.AuthServiceGrpc",
                "com.learnwords.users.v1.UserRelationsServiceGrpc",
                "com.learnwords.groups.v1.StudentGroupServiceGrpc",
                "com.learnwords.vocabulary.v1.VocabularyReadServiceGrpc"
        }) {
            hints.reflection().registerTypeIfPresent(loader, fqn,
                    MemberCategory.INVOKE_DECLARED_METHODS,
                    MemberCategory.INVOKE_PUBLIC_METHODS);
        }

        // Polymorphic Jackson request/response types (sealed UserAnswer + @JsonSubTypes,
        // algorithm result hierarchy) — register the whole evaluationService graph for binding.
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(loader);
        CachingMetadataReaderFactory readers = new CachingMetadataReaderFactory(loader);
        try {
            Resource[] resources = resolver.getResources(
                    "classpath*:com/learnwords/deckservice/service/evaluationService/**/*.class");
            for (Resource res : resources) {
                try {
                    String className = readers.getMetadataReader(res).getClassMetadata().getClassName();
                    Class<?> type = Class.forName(className, false, loader);
                    binding.registerReflectionHints(hints.reflection(), type);
                } catch (Throwable ignored) {
                    // Skip unreadable/unresolvable entries.
                }
            }
        } catch (Exception ignored) {
            // Nothing to register.
        }
    }
}
