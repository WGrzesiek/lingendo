package com.learnwords.deckservice.config;

import com.learnwords.deckservice.dto.facade.review.ReviewCounters;
import com.learnwords.deckservice.dto.session.FlashcardSessionNumber;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * Native reflection hints for deck-service.
 * <ul>
 *   <li>JPQL {@code select new ...} constructor-expression DTOs (Hibernate resolves the
 *       constructor reflectively at query-compile time).</li>
 *   <li>net.devh {@code @GrpcClient} stub injection: the BeanPostProcessor sets the stub
 *       field reflectively and builds it via {@code <Service>Grpc.newBlockingStub(channel)};
 *       without hints the fields stay null and calls NPE at runtime.</li>
 * </ul>
 */
public class DeckNativeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
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
            hints.reflection().registerTypeIfPresent(classLoader, fqn,
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
            hints.reflection().registerTypeIfPresent(classLoader, fqn,
                    MemberCategory.INVOKE_DECLARED_METHODS,
                    MemberCategory.INVOKE_PUBLIC_METHODS);
        }
    }
}
